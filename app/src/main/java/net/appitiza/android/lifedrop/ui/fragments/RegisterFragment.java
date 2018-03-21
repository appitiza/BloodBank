package net.appitiza.android.lifedrop.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

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
import net.appitiza.android.lifedrop.ui.activities.HomeActivity;
import net.appitiza.android.lifedrop.ui.callbacks.StartupAction;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;
import net.appitiza.android.lifedrop.utils.GeneralUtils;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class RegisterFragment extends BaseFragment implements View.OnClickListener, WebserviceCallBack
        , AdapterView.OnItemSelectedListener, PlaceSelectionListener, ValidationDialogCallback {

    private WeakReference<Context> mWeakActivity;
    private CustomTextview mTvRegister;
    private CustomTextview mTvLogin;
    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtMobile;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private RadioButton mRbMale;
    private RadioButton mRbFeMale;
    private Spinner mSpnrBlood;
    private EditText mEtLastDonated;
    private EditText mEtMedical;
    private EditText mEtAddress;
    private CheckBox mCbTerms;
    private CustomTextview mTvTerms;
    private final ArrayList<String> blood_list = new ArrayList<>();
    private String blood = "";
    private String phoneNumber = "";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 301;
    private Place mPlace;
    private final int REGISTRATION = 1;
    private ProgressDialog pd;
    private static int datetype;
    private Dialog mDialogRespond;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String fcm_token = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        initialize(v);
        firbaseAuth();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mWeakActivity = new WeakReference<>(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void initialize(View v) {

        blood_list.add(getString(R.string.select_a_blood_type));
        blood_list.add("A+");
        blood_list.add("A-");
        blood_list.add("B+");
        blood_list.add("B-");
        blood_list.add("AB+");
        blood_list.add("AB-");
        blood_list.add("O+");
        blood_list.add("O-");

        mTvRegister = v.findViewById(R.id.tv_register);
        mTvLogin = v.findViewById(R.id.tv_login);
        mEtFirstName = v.findViewById(R.id.et_firstname);
        mEtLastName = v.findViewById(R.id.et_lastname);
        mEtMobile = v.findViewById(R.id.et_mobile);
        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
        mEtConfirmPassword = v.findViewById(R.id.et_confirmpassword);
        mRbMale = v.findViewById(R.id.rb_male);
        mRbFeMale = v.findViewById(R.id.rb_female);
        mSpnrBlood = v.findViewById(R.id.spnr_blood);
        mEtLastDonated = v.findViewById(R.id.et_last_donation);
        mEtMedical = v.findViewById(R.id.et_medical_issue);
        mEtAddress = v.findViewById(R.id.et_address);
        mCbTerms = v.findViewById(R.id.cb_terms);
        mTvTerms = v.findViewById(R.id.tv_cb);

        ArrayAdapter bloodgroups = new ArrayAdapter<>(mWeakActivity.get(), android.R.layout.simple_spinner_item, blood_list);
        bloodgroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrBlood.setAdapter(bloodgroups);
        mSpnrBlood.setOnItemSelectedListener(this);
        mTvRegister.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        mTvTerms.setOnClickListener(this);
        mEtLastDonated.setOnClickListener(this);
        mEtMedical.setOnClickListener(this);
        mEtAddress.setOnClickListener(this);

    }

    private void firbaseAuth() {
        FirebaseApp.initializeApp(getActivity());
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
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if (((FirebaseAuthInvalidCredentialsException) e).getErrorCode().equalsIgnoreCase("ERROR_INVALID_PHONE_NUMBER")) {
                    showAlert(getString(R.string.empty_field), getString(R.string.empty_mobile), RegisterFragment.this, mEtMobile);
                } else {
                    showAlert(getString(R.string.unexpected_error), getString(R.string.error_verification_please_correct_after), RegisterFragment.this, mTvRegister, Constants.CONTINUE_REGISTRATION_DIALOG_ID);

                }


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
        if (currentUser != null) {
            phoneNumber = currentUser.getPhoneNumber();
            mEtMobile.setEnabled(false);
            mEtMobile.setFocusable(false);
            mEtMobile.setClickable(false);
            mEtMobile.setText(phoneNumber);
        } else {
            mEtMobile.setEnabled(true);
            mEtMobile.setFocusable(true);
            mEtMobile.setClickable(true);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mWeakActivity.get(), new OnCompleteListener<AuthResult>() {
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
                                if (task.getException() != null)
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
        mDialogRespond = new Dialog(mWeakActivity.get());
        mDialogRespond.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((Activity)mWeakActivity.get()).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialogRespond.setCancelable(true);
        mDialogRespond.setContentView(R.layout.verify_dialog);
        if (mDialogRespond.getWindow() != null && mDialogRespond.getWindow().getAttributes() != null) {
            mDialogRespond.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationGrow;
        }
        CustomTextview mTvMessage = mDialogRespond.findViewById(R.id.tv_message);
        CustomTextview mTvSubmit = mDialogRespond.findViewById(R.id.tv_submit);
        CustomTextview mTvResend = mDialogRespond.findViewById(R.id.tv_resend);
        final EditText mEtCode = mDialogRespond.findViewById(R.id.et_code);
        mTvMessage.setText(getString(R.string.verify_message, mEtMobile.getText().toString()));
        mTvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEtCode.getText().toString();
                if (!TextUtils.isEmpty(code)) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = new ProgressDialog(getActivity());
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
            case R.id.tv_register:
                verifyNumber();
                break;
            case R.id.tv_login:
                ((StartupAction) mWeakActivity.get()).login();
                break;
            case R.id.et_last_donation:
                datetype = 1;
                showDateSelector();
                break;
            case R.id.et_medical_issue:
                datetype = 2;
                showDateSelector();
                break;
            case R.id.et_address:
                startLocationIntent();
                break;
            case R.id.tv_cb:
                String url = "http://appitiza.net/dev/bloodbank/webservices/termscondition.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

    private void verifyNumber() {
        clearValidation();
        if (TextUtils.isEmpty(mEtFirstName.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_firstname), RegisterFragment.this, mEtFirstName);
        } /*else if (TextUtils.isEmpty(mEtLastName.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_lastname), RegisterFragment.this, mEtLastName);
        }*/ else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_email), RegisterFragment.this, mEtEmail);
        } else if (!GeneralUtils.isValidEmail(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_email_valid), RegisterFragment.this, mEtEmail);
        } else if (TextUtils.isEmpty(mEtMobile.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_mobile), RegisterFragment.this, mEtMobile);
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_Password), RegisterFragment.this, mEtPassword);
        } /*else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.confirm_password), RegisterFragment.this, mEtConfirmPassword);
        } else if (!mEtConfirmPassword.getText().toString().trim().equals(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.password_do_not_match), RegisterFragment.this, mEtConfirmPassword);
        }*/ else if (TextUtils.isEmpty(blood.trim()) || blood.trim().equalsIgnoreCase(getString(R.string.select_a_blood_type))) {
            showAlert(getString(R.string.empty_field), getString(R.string.select_a_blood_type), RegisterFragment.this, mSpnrBlood);
        } else if (TextUtils.isEmpty(mEtMedical.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_medical), RegisterFragment.this, mEtMedical);
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_address), RegisterFragment.this, mEtAddress);
        } else if (!mCbTerms.isChecked()) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_agree_terms), RegisterFragment.this, mCbTerms);
        } else {

            if (NetWorkUtil.isNetworkAvailable(mWeakActivity.get())) {

                if (mPlace != null) {

                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("verifying number...");
                    pd.show();
                    startPhoneNumberVerification(mEtMobile.getText().toString().trim());

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
                (Activity) mWeakActivity.get(),               // Activity (for callback binding)
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
                (Activity) mWeakActivity.get(),               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void register() {
        Log.d("TAG", "register called");
        clearValidation();
        if (TextUtils.isEmpty(mEtFirstName.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_firstname), RegisterFragment.this, mEtFirstName);
        } /*else if (TextUtils.isEmpty(mEtLastName.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_lastname), RegisterFragment.this, mEtLastName);
        }*/ else if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_email), RegisterFragment.this, mEtEmail);
        } else if (!GeneralUtils.isValidEmail(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_email_valid), RegisterFragment.this, mEtEmail);
        } else if (TextUtils.isEmpty(mEtMobile.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_mobile), RegisterFragment.this, mEtMobile);
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_Password), RegisterFragment.this, mEtPassword);
        } /*else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.confirm_password), RegisterFragment.this, mEtConfirmPassword);
        } else if (!mEtConfirmPassword.getText().toString().trim().equals(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.password_do_not_match), RegisterFragment.this, mEtConfirmPassword);
        }*/ else if (TextUtils.isEmpty(mEtMedical.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_medical), RegisterFragment.this, mEtMedical);
        } else if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_address), RegisterFragment.this, mEtAddress);
        } else if (!mCbTerms.isChecked()) {
            showAlert(getString(R.string.empty_field), getString(R.string.empty_agree_terms), RegisterFragment.this, mCbTerms);
        } else {

            if (NetWorkUtil.isNetworkAvailable(getContext())) {

                if (mPlace != null) {
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage(getString(R.string.registering));
                    pd.show();
                    String latitude = "" + mPlace.getLatLng().latitude;
                    String longitude = "" + mPlace.getLatLng().longitude;
                    String address = "" + mPlace.getAddress();
                    String gender = (mRbMale.isChecked() ? Constants.MALE_CODE : Constants.FEMALE_CODE);
                    phoneNumber = mEtMobile.getText().toString();
                    fcm_token = FirebaseInstanceId.getInstance().getToken();
                    if (fcm_token == null) {
                        fcm_token = "";
                    }
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        WebserviceHandler.signUp(fcm_token, mEtEmail.getText().toString().trim(), mEtFirstName.getText().toString().trim(), mEtLastName.getText().toString().trim(), mEtPassword.getText().toString().trim(), phoneNumber, latitude, longitude, address, gender, blood, mEtMedical.getText().toString().trim(), mEtLastDonated.getText().toString().trim(), RegisterFragment.this, REGISTRATION);
                    } else {
                        showAlert(getString(R.string.empty_field), getString(R.string.empty_mobile));
                    }
                }
            } else {
                showNetworkAlert();
            }

        }
    }

    private void clearValidation() {
        mEtFirstName.setBackgroundResource(R.drawable.et_bg);
        mEtLastName.setBackgroundResource(R.drawable.et_bg);
        mEtEmail.setBackgroundResource(R.drawable.et_bg);
        mEtMobile.setBackgroundResource(R.drawable.et_bg);
        mEtPassword.setBackgroundResource(R.drawable.et_bg);
        mEtConfirmPassword.setBackgroundResource(R.drawable.et_bg);
        mEtAddress.setBackgroundResource(R.drawable.et_bg);
    }

    private void startLocationIntent() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_FULLSCREEN)
                    .build((Activity) mWeakActivity.get());
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

        DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(mWeakActivity.get(),
                new android.app.DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int mMonth = monthOfYear + 1;
                        String datetime = dayOfMonth + "-" + mMonth + "-" + year;

                        if (datetype == 1) {
                            mEtLastDonated.setText(datetime);
                        } else {
                            mEtMedical.setText(datetime);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setTitle(null);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }


    @Override
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        if (requestCode == REGISTRATION) {
            if (response != null) {
                if (response.getErrorcode() == 0) {
                    SignInModel signInModel = ResponseParser.parseSignIn(response);
                    CacheUtility.writeCache(getActivity(), CacheConstants.PROFILE, signInModel);
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(getActivity(), CacheConstants.PROFILE));
                    moveToNextActivity(true, HomeActivity.class, null);
                } else {
                    showAlert("Error", response.getMessage());
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
                    Place place = PlaceAutocomplete.getPlace(mWeakActivity.get(), data);
                    this.onPlaceSelected(place);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(mWeakActivity.get(), data);
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

    @Override
    public void onOkClick(View v, int actionid) {
        if (v != null) {
            if (v == mEtMedical) {
                datetype = 2;
                showDateSelector();
            } else if (v == mEtLastDonated) {
                datetype = 1;
                showDateSelector();
            } else if (v == mEtAddress) {
                startLocationIntent();
            } else if (v == mTvRegister) {
                if (actionid == Constants.CONTINUE_REGISTRATION_DIALOG_ID) {
                    register();
                }
            } else {
                v.requestFocus();
            }
        }
    }
}
