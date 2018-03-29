package net.appitiza.android.lifedrop.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RequestFragment extends BaseFragment implements View.OnClickListener, WebserviceCallBack, AdapterView.OnItemSelectedListener, PlaceSelectionListener {

    private CustomTextview mTvRequest;
    private EditText mEtMessage;
    private Spinner mSpnrBlood;
    private EditText mEtRequiredDate;
    private EditText mEtAddress;

    String[] blood_list = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private String blood = "A+";
    private String phoneNumber = "";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 301;
    private Place mPlace;
    private int REQUEST = 1;
    //private Animation animShake;
    private ProgressDialog pd;
    private static final String DATEPICKER_TAG = "datepicker";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false);
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
        final Calendar calendar = Calendar.getInstance();

        //animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        mTvRequest = getActivity().findViewById(R.id.tv_request);
        mEtMessage = getActivity().findViewById(R.id.et_message);
        mSpnrBlood = getActivity().findViewById(R.id.spnr_blood);
        mEtRequiredDate = getActivity().findViewById(R.id.et_required_date);
        mEtAddress = getActivity().findViewById(R.id.et_address);

        ArrayAdapter bloodgroups = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, blood_list);
        bloodgroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrBlood.setAdapter(bloodgroups);

        mSpnrBlood.setOnItemSelectedListener(this);
        mTvRequest.setOnClickListener(this);
        mEtRequiredDate.setOnClickListener(this);
        mEtAddress.setOnClickListener(this);
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
            case R.id.tv_request:
                request();
                break;
            case R.id.et_address:
                startLocationIntent();
                break;
            case R.id.et_required_date:
                showDateSelector();
                break;
            case R.id.tv_cb:
                String url = "http://www.google.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

    private void showDateSelector() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(getActivity(),
                new android.app.DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int mMonth = monthOfYear + 1;
                        String datetime = dayOfMonth + "-" + mMonth + "-" + year;

                        try {
                            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(datetime);
                            String dateString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                            Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);

                            if (date1.before(date2)) {
                                showAlert(getString(R.string.error), getString(R.string.select_future_date));
                            } else if (date1.after(date2)) {
                                mEtRequiredDate.setText(datetime);
                            } else {
                                mEtRequiredDate.setText(datetime);
                            }
                        } catch (ParseException e) {
                            showAlert(getString(R.string.error), getString(R.string.unexpected_error));
                        }
                    }
                }, mYear, mMonth, mDay);
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.setTitle(null);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void request() {
        clearValidation();
         if (TextUtils.isEmpty(mEtMessage.getText().toString().trim())) {
        //    mEtMessage.startAnimation(animShake);
            showAlert(getString(R.string.app_name), getString(R.string.empty_message));
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
         //   mEtAddress.startAnimation(animShake);
            showAlert(getString(R.string.app_name), getString(R.string.empty_address));
        } else {

            if (NetWorkUtil.isNetworkAvailable(getContext())) {

                if (mPlace != null) {
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Broadcasting Your Request...");
                    pd.show();
                    String latitude = "" + mPlace.getLatLng().latitude;
                    String longitude = "" + mPlace.getLatLng().longitude;
                    String address = "" + mPlace.getAddress();
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(getActivity(), CacheConstants.PROFILE));
                    if (Bloodbank.getSignInData() != null && Bloodbank.getSignInData().getEmail() != null) {

                        WebserviceHandler.requestBlood(Bloodbank.getSignInData().getEmail(),blood, mEtMessage.getText().toString().trim(),address, latitude, longitude, mEtRequiredDate.getText().toString().trim(),RequestFragment.this, REQUEST);
                    }
                }
            } else {
                showNetworkAlert();
            }

        }
    }

    private void clearValidation() {
        mEtMessage.setBackgroundResource(R.drawable.et_bg);
        mEtAddress.setBackgroundResource(R.drawable.et_bg);
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
        if (requestCode == REQUEST) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    showAlert(getString(R.string.success),getString(R.string.message_sent));
                } else {
                    showAlert(getString(R.string.error), response.getMessage());
                }

            }
        }
    }

    @Override
    public void connectionError(String msg, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        showAlert(getString(R.string.error), msg);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        blood = blood_list[i];
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
    }

    @Override
    public void onError(Status status) {

    }

}
