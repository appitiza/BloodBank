package net.appitiza.android.lifedrop.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.AppLog;
import net.appitiza.android.lifedrop.model.SearchItem;
import net.appitiza.android.lifedrop.model.SearchListModel;
import net.appitiza.android.lifedrop.ui.adapter.SearchListAdapter;
import net.appitiza.android.lifedrop.ui.callbacks.HomeActivityActions;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.util.ArrayList;


public class SearchFragment extends BaseFragment implements View.OnClickListener, WebserviceCallBack, ValidationDialogCallback, AdapterView.OnItemSelectedListener, PlaceSelectionListener {

    private CustomTextview mTvSearch;
    private Spinner mSpnrBlood;
    private EditText mEtAddress;
    ArrayList<String> blood_list = new ArrayList<String>();
    private String blood = "";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 301;
    private Place mPlace;
    private int REGISTRATION = 1;
    //private Animation animShake;
    private ProgressDialog pd;
    private final ArrayList<SearchItem> mList = new ArrayList<>();
    private RecyclerView mRecylerview;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //endless scroll
    private int previousTotal = 0;
    private boolean loading = true;
    private static final int visibleThreshold = 2;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private int mNextCall = 0;
    private String limit = "0";

    private static final int SEARCH_LIST = 1;
    private static final int SEARCH_LIST_REFRESH = 2;
    private SearchListAdapter mAdapter;
    private int SEARCH_BLOOD = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void initialize() {
        blood_list.add(getString(R.string.select_a_blood_type));
        blood_list.add("A+");
        blood_list.add("A-");
        blood_list.add("B+");
        blood_list.add("B-");
        blood_list.add("AB+");
        blood_list.add("AB-");
        blood_list.add("O+");
        blood_list.add("O-");
        mTvSearch = getActivity().findViewById(R.id.tv_search);
        mSpnrBlood = getActivity().findViewById(R.id.spnr_blood);
        mEtAddress = getActivity().findViewById(R.id.et_address);

        ArrayAdapter bloodgroups = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, blood_list);
        bloodgroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrBlood.setAdapter(bloodgroups);

        mSpnrBlood.setOnItemSelectedListener(this);
        mTvSearch.setOnClickListener(this);
        mEtAddress.setOnClickListener(this);


        mAdapter = new SearchListAdapter(getActivity(), mList);
        mRecylerview = getActivity().findViewById(R.id.rv_list);
        mSwipeRefreshLayout = getActivity().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecylerview.setLayoutManager(mLayoutManager);
        mRecylerview.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                limit = "0";
                getData(SEARCH_LIST_REFRESH);
            }
        });

        mRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                infiniteScroll(recyclerView);
            }
        });
        mRecylerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    ((HomeActivityActions) getActivity()).listScrolling(false);
                } else {
                    ((HomeActivityActions) getActivity()).listScrolling(true);
                }
            }


        });

    }

    private void getData(int type) {


        if (TextUtils.isEmpty(blood.trim()) || blood.trim().equalsIgnoreCase(getString(R.string.select_a_blood_type))) {
            showAlert(getString(R.string.app_name), getString(R.string.select_a_blood_type), SearchFragment.this, mSpnrBlood);
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_address), SearchFragment.this, mEtAddress);
        } else {
            if (mPlace != null) {
                if (NetWorkUtil.isNetworkAvailable(getContext())) {
                    if (type == SEARCH_LIST) {
                        pd = new ProgressDialog(getActivity());
                        pd.setMessage("Searching around you...");
                        pd.show();
                    }
                    WebserviceHandler.searchBlood("" + mPlace.getLatLng().latitude, "" + mPlace.getLatLng().longitude, limit, blood, SearchFragment.this, type);
                } else {
                    showNetworkAlert();
                }
            }
        }

    }

    private void infiniteScroll(RecyclerView recyclerView) {
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();


        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // load next set
            loading = true;
            if (mNextCall == 1) {
                getData(SEARCH_LIST);

            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search:
                limit = "0";
                mList.clear();
                getData(SEARCH_BLOOD);
                break;

            case R.id.et_address:
                startLocationIntent();
                break;

        }
    }


    private void startLocationIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            AppLog.logErrorString(e.getMessage());
        }
    }

    @Override
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (requestCode == SEARCH_LIST) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    SearchListModel ItemListModel = ResponseParser.parseSearch(response);
                    if (ItemListModel != null && ItemListModel.getList() != null && ItemListModel.getList().size() > 0) {
                        mList.addAll(ItemListModel.getList());
                        mAdapter.notifyDataSetChanged();
                        mNextCall = ItemListModel.getNext();
                        limit = mList.get(mList.size() - 1).getId();
                    }
                }

            }
        } else if (requestCode == SEARCH_LIST_REFRESH) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    SearchListModel ItemListModel = ResponseParser.parseSearch(response);
                    if (ItemListModel != null && ItemListModel.getList() != null && ItemListModel.getList().size() > 0) {
                        mList.clear();
                        mList.addAll(ItemListModel.getList());
                        mAdapter.notifyDataSetChanged();
                        mNextCall = ItemListModel.getNext();
                        limit = mList.get(mList.size() - 1).getId();
                        previousTotal = 0;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

            }
        }
    }

    @Override
    public void connectionError(String msg, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        showAlert("Error", msg);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        blood = blood_list.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    this.onPlaceSelected(place);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                    this.onError(status);
                }
                break;
        }

    }

    @Override
    public void onPlaceSelected(Place place) {
        mEtAddress.setText(place.getAddress());
        mPlace = place;
        limit = "0";
        mList.clear();
        if (!TextUtils.isEmpty(blood.trim()) && !blood.trim().equalsIgnoreCase(getString(R.string.select_a_blood_type))) {
            getData(SEARCH_BLOOD);
        }

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onOkClick(View v, int actionid) {
        if (v != null) {
            if (v == mEtAddress) {
                startLocationIntent();
            } else {
                v.requestFocus();
            }
        }
    }
}
