package net.appitiza.android.lifedrop.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.model.RequestListModel;
import net.appitiza.android.lifedrop.model.Requestlist;
import net.appitiza.android.lifedrop.ui.adapter.RequestListAdapter;
import net.appitiza.android.lifedrop.ui.callbacks.HomeActivityActions;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.util.ArrayList;


public class MessageFragment extends BaseFragment implements WebserviceCallBack {


    private int REGISTRATION = 1;
    //private Animation animShake;
    private ProgressDialog pd;


    private final ArrayList<Requestlist> mList = new ArrayList<>();
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
    private static final int ENDLESS = 3;
    private RequestListAdapter mAdapter;
    private int SEARCH_MESSAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        initialize(v);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void initialize(View v) {
        //animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        mAdapter = new RequestListAdapter(getActivity(), mList);
        mRecylerview = v.findViewById(R.id.rv_list);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
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
        getData(SEARCH_LIST);
    }

    private void getData(int type) {

        if (NetWorkUtil.isNetworkAvailable(getContext())) {
            if (type == SEARCH_LIST) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("Fetching messages...");
                pd.show();
            } else if (type == ENDLESS) {
                if (mList.get(mList.size() - 1) != null) {
                    mList.add(null);
                }
                mAdapter.notifyDataSetChanged();
            }
            WebserviceHandler.searchMessage(limit, MessageFragment.this, type);

        } else {
            showNetworkAlert();
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
                getData(ENDLESS);

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
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (requestCode == SEARCH_LIST) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    RequestListModel ItemListModel = ResponseParser.parseMessage(response);
                    if (ItemListModel != null && ItemListModel.getRequestlist() != null && ItemListModel.getRequestlist().size() > 0) {
                        mList.addAll(ItemListModel.getRequestlist());
                        mAdapter.notifyDataSetChanged();
                        mNextCall = ItemListModel.getNext();
                        limit = mList.get(mList.size() - 1).getId();
                    }
                }

            }
        } else if (requestCode == SEARCH_LIST_REFRESH) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    RequestListModel ItemListModel = ResponseParser.parseMessage(response);
                    if (ItemListModel != null && ItemListModel.getRequestlist() != null && ItemListModel.getRequestlist().size() > 0) {
                        mList.clear();
                        mList.addAll(ItemListModel.getRequestlist());
                        mAdapter.notifyDataSetChanged();
                        mNextCall = ItemListModel.getNext();
                        limit = mList.get(mList.size() - 1).getId();
                        previousTotal = 0;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

            }
        } else if (requestCode == ENDLESS) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    RequestListModel ItemListModel = ResponseParser.parseMessage(response);
                    if (ItemListModel != null && ItemListModel.getRequestlist() != null && ItemListModel.getRequestlist().size() > 0) {
                        mList.remove(mList.size() - 1);
                        mList.addAll(ItemListModel.getRequestlist());
                        mAdapter.notifyDataSetChanged();
                        mNextCall = ItemListModel.getNext();
                        limit = mList.get(mList.size() - 1).getId();
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


}
