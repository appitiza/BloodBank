package net.appitiza.android.lifedrop.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.AppLog;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.constants.Constants;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.observer.ObserverData;
import net.appitiza.android.lifedrop.observer.UserDataRepository;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;
import net.appitiza.android.lifedrop.utils.GeneralUtils;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UpdateActivity extends BaseActivity implements View.OnClickListener, WebserviceCallBack
        , PlaceSelectionListener, ValidationDialogCallback {

    private Toolbar mToolbar;
    private CustomTextview mTvTitle;
    private CustomTextview mTvDone;

    private EditText mEtFullName;
    private EditText mEtMobile;
    private EditText mEtEmail;
    private RadioButton mRbMale;
    private RadioButton mRbFeMale;
    private EditText mEtLastDonated;
    private EditText mEtDOB;
    private EditText mEtAddress;

    private String phoneNumber = "";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 301;
    private Place mPlace;
    private int UPDATION = 1;
    private ProgressDialog pd;
    private static final String DATEPICKER_TAG = "datepicker";
    private static int datetype;
    private Dialog mDialogRespond;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String fcm_token = "";
    private String blood = "";
    private String latitude = "";
    private String longitude = "";
    private String address = "";

    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initialize();
        firbaseAuth();
    }

    private void initialize() {

        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.edit_profile));
            mTvDone = findViewById(R.id.tv_done);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        final Calendar calendar = Calendar.getInstance();
        mEtFullName = findViewById(R.id.et_fullname);
        mEtMobile = findViewById(R.id.et_mobile);
        mEtEmail = findViewById(R.id.et_email);
        mRbMale = findViewById(R.id.rb_male);
        mRbFeMale = findViewById(R.id.rb_female);
        mEtLastDonated = findViewById(R.id.et_last_donation);
        mEtDOB = findViewById(R.id.et_dob);
        mEtAddress = findViewById(R.id.et_address);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mEtLastDonated.setOnClickListener(this);
        mEtAddress.setOnClickListener(this);
        mEtDOB.setOnClickListener(this);
        mTvDone.setOnClickListener(this);
        if (Bloodbank.getSignInData() != null) {
            mEtFullName.setText(Bloodbank.getSignInData().getFirstName());
            mEtFullName.setSelection(mEtFullName.getText().length());
            mEtMobile.setText(Bloodbank.getSignInData().getNumber());
            phoneNumber = Bloodbank.getSignInData().getNumber();
            mEtEmail.setText(Bloodbank.getSignInData().getEmail());

            mEtLastDonated.setText(Bloodbank.getSignInData().getLastDonation());
            mEtDOB.setText(Bloodbank.getSignInData().getMedicalIssue());
            mEtAddress.setText(Bloodbank.getSignInData().getAddress());
            address = Bloodbank.getSignInData().getAddress();
            latitude = Bloodbank.getSignInData().getLat();
            longitude = Bloodbank.getSignInData().getLon();
            password = Bloodbank.getSignInData().getPassword();
            if (Bloodbank.getSignInData().getGender().equalsIgnoreCase(Constants.MALE_CODE)) {
                mRbMale.setChecked(true);
            } else {
                mRbFeMale.setChecked(true);
            }

        }

    }

    private void firbaseAuth() {
        FirebaseApp.initializeApp(UpdateActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d("TAG", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("TAG", "onVerificationFailed:" + e);
                if(pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                showUnexpectedError();

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("TAG", "onCodeSent:" + verificationId);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                mVerificationId = verificationId;
                mResendToken = token;
                showVerifyDialog();
            }
        };

        FirebaseUser currentUser = mAuth.getCurrentUser();
       /* if (currentUser != null) {
            phoneNumber = currentUser.getPhoneNumber();
            mEtMobile.setEnabled(false);
            mEtMobile.setFocusable(false);
            mEtMobile.setClickable(false);
            mEtMobile.setText(phoneNumber);
        } else {
            mEtMobile.setEnabled(true);
            mEtMobile.setFocusable(true);
            mEtMobile.setClickable(true);
        }*/
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(UpdateActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "task completed");

                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }

                            FirebaseUser currentUser = task.getResult().getUser();
                            if (currentUser != null) {
                                phoneNumber = currentUser.getPhoneNumber();
                                register();
                            }

                        } else {
                            if (pd != null && pd.isShowing()) {
                                pd.dismiss();
                            }

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                showAlert(getString(R.string.error), getString(R.string.verify_invalid_code));
                            } else {
                                showAlert(getString(R.string.error), task.getException().toString());
                            }
                        }
                    }
                });
    }


    private void showVerifyDialog() {
        if (mDialogRespond != null && mDialogRespond.isShowing()) {
            mDialogRespond.dismiss();
        }
        mDialogRespond = new Dialog(UpdateActivity.this);
        mDialogRespond.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogRespond.setCancelable(true);
        mDialogRespond.setContentView(R.layout.verify_dialog);
        if (mDialogRespond.getWindow() != null && mDialogRespond.getWindow().getAttributes() != null) {
            mDialogRespond.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationGrow;
        }
        CustomTextview mTvMessage = mDialogRespond.findViewById(R.id.tv_message);
        CustomTextview mTvSubmit = mDialogRespond.findViewById(R.id.tv_submit);
        CustomTextview mTvResend = mDialogRespond.findViewById(R.id.tv_resend);
        final EditText mEtCode = mDialogRespond.findViewById(R.id.et_code);
        mTvMessage.setText(getString(R.string.verify_message) + mEtMobile.getText().toString());
        mTvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEtCode.getText().toString();
                if (!TextUtils.isEmpty(code)) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = new ProgressDialog(UpdateActivity.this);
                    pd.setMessage("verifying code...");
                    pd.show();
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }


            }
        });
        mTvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(mEtMobile.getText().toString(), mResendToken);
            }
        });
        mDialogRespond.show();
    }

    private void dismissVerifyDialog() {
        if (mDialogRespond != null && mDialogRespond.isShowing()) {
            mDialogRespond.dismiss();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_done:
                if (Bloodbank.getSignInData() != null && mEtMobile.getText().toString().trim().equalsIgnoreCase(Bloodbank.getSignInData().getNumber())) {
                    register();
                } else {
                    verifyNumber();

                }
                break;

            case R.id.et_last_donation:
                datetype = 1;
                showDateSelector();
                break;
            case R.id.et_dob:
                datetype = 2;
                showDateSelector();
                break;
            case R.id.et_address:
                startLocationIntent();
                break;

        }
    }

    private void verifyNumber() {
        clearValidation();
        if (TextUtils.isEmpty(mEtFullName.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_firstname), UpdateActivity.this, mEtFullName, Constants.VALIDATION_DIALOG_ID);
        }  else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_email), UpdateActivity.this, mEtEmail, Constants.VALIDATION_DIALOG_ID);
        } else if (!GeneralUtils.isValidEmail(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_email_valid), UpdateActivity.this, mEtEmail, Constants.VALIDATION_DIALOG_ID);
        } else if (TextUtils.isEmpty(mEtMobile.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_mobile), UpdateActivity.this, mEtMobile, Constants.VALIDATION_DIALOG_ID);
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_address), UpdateActivity.this, mEtAddress, Constants.VALIDATION_DIALOG_ID);
        } else {

            if (NetWorkUtil.isNetworkAvailable(UpdateActivity.this)) {

                if (address != null && latitude != null && longitude != null && !address.equalsIgnoreCase("") && !latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {

                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = new ProgressDialog(UpdateActivity.this);
                    pd.setMessage("verifying number...");
                    pd.show();
                    startPhoneNumberVerification(mEtMobile.getText().toString().trim());

                } else {
                    showAlert(getString(R.string.app_name), getString(R.string.empty_address));
                }
            } else {
                showNetworkAlert();
            }

        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                UpdateActivity.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        dismissVerifyDialog();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                UpdateActivity.this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void register() {
        Log.d("TAG", "register called");
        clearValidation();
        if (TextUtils.isEmpty(mEtFullName.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_firstname), UpdateActivity.this, mEtFullName, Constants.VALIDATION_DIALOG_ID);
        }  else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_email), UpdateActivity.this, mEtEmail, Constants.VALIDATION_DIALOG_ID);
        } else if (!GeneralUtils.isValidEmail(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_email_valid), UpdateActivity.this, mEtEmail, Constants.VALIDATION_DIALOG_ID);
        } else if (TextUtils.isEmpty(mEtMobile.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_mobile), UpdateActivity.this, mEtMobile, Constants.VALIDATION_DIALOG_ID);
        } else if (TextUtils.isEmpty(mEtDOB.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_medical), UpdateActivity.this, mEtDOB, Constants.VALIDATION_DIALOG_ID);
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_address), UpdateActivity.this, mEtAddress, Constants.VALIDATION_DIALOG_ID);
        } else {

            if (NetWorkUtil.isNetworkAvailable(UpdateActivity.this)) {

                if (address != null && latitude != null && longitude != null && !address.equalsIgnoreCase("") && !latitude.equalsIgnoreCase("") && !longitude.equalsIgnoreCase("")) {
                    pd = new ProgressDialog(UpdateActivity.this);
                    pd.setMessage("Updating...");
                    pd.show();
                    fcm_token = FirebaseInstanceId.getInstance().getToken();
                    String gender = (mRbMale.isChecked() ? Constants.MALE_CODE : Constants.FEMALE_CODE);
                    phoneNumber = mEtMobile.getText().toString();
                    if (fcm_token == null) {
                        fcm_token = "";
                    }
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        if (Bloodbank.getSignInData() != null) {
                            blood = Bloodbank.getSignInData().getBlood();
                            WebserviceHandler.updateProfile(fcm_token, Bloodbank.getSignInData().getEmail(), mEtFullName.getText().toString().trim(), password, phoneNumber, latitude, longitude, address, gender, blood, mEtDOB.getText().toString().trim(), mEtLastDonated.getText().toString().trim(), UpdateActivity.this, UPDATION);

                        }
                    } else {
                        showAlert(getString(R.string.app_name), getString(R.string.empty_mobile));
                    }
                }
            } else {
                showNetworkAlert();
            }

        }
    }

    private void clearValidation() {
        mEtFullName.setBackgroundResource(R.drawable.et_bg);
        mEtEmail.setBackgroundResource(R.drawable.et_bg);
        mEtMobile.setBackgroundResource(R.drawable.et_bg);
        mEtAddress.setBackgroundResource(R.drawable.et_bg);
    }

    private void startLocationIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(UpdateActivity.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            AppLog.logErrorString(e.getMessage());
        }
    }

    private void showDateSelector() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this,
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
                                if (datetype == 1) {
                                    mEtLastDonated.setText(datetime);
                                } else {
                                    mEtDOB.setText(datetime);
                                }
                            } else if (date1.after(date2)) {
                                showAlert(getString(R.string.error), getString(R.string.select_past_date));
                            } else {
                                if (datetype == 1) {
                                    mEtLastDonated.setText(datetime);
                                } else {
                                    mEtDOB.setText(datetime);
                                }
                            }
                        } catch (ParseException e) {
                            showAlert(getString(R.string.error), getString(R.string.unexpected_error));
                        }
                    }
                }, mYear, mMonth, mDay);
        if (datetype == 2) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        }

        datePickerDialog.setTitle(null);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    @Override
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (requestCode == UPDATION) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    SignInModel signInModel = ResponseParser.parseSignIn(response);
                    CacheUtility.writeCache(UpdateActivity.this, CacheConstants.PROFILE, signInModel);
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(UpdateActivity.this, CacheConstants.PROFILE));
                    showAlert(getString(R.string.success), getString(R.string.profile_updated), UpdateActivity.this, mTvDone, Constants.UPDATE_COMPLETE_DIALOG_ID);
                    UserDataRepository.getInstance().notifyObservers(new ObserverData());

                } else {
                    showAlert(getString(R.string.error), response.getMessage(), UpdateActivity.this, mTvDone, Constants.UPDATE_COMPLETE_DIALOG_ID);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(UpdateActivity.this, data);
                    this.onPlaceSelected(place);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(UpdateActivity.this, data);
                    this.onError(status);
                }
                break;
        }

    }

    @Override
    public void onPlaceSelected(Place place) {
        mEtAddress.setText(place.getAddress());
        mPlace = place;
        latitude = "" + mPlace.getLatLng().latitude;
        longitude = "" + mPlace.getLatLng().longitude;
        address = "" + mPlace.getAddress();
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onOkClick(View v, int action) {
        if (v != null) {
            if (v == mEtDOB) {
                datetype = 2;
                showDateSelector();
            } else if (v == mEtLastDonated) {
                datetype = 1;
                showDateSelector();
            } else if (v == mEtAddress) {
                startLocationIntent();
            } else if (v == mTvDone) {
                if (action == Constants.UPDATE_COMPLETE_DIALOG_ID)
                    onBackPressed();
            } else {
                v.requestFocus();
            }
        }
    }
}
