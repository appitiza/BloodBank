package net.appitiza.android.lifedrop.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, WebserviceCallBack, ValidationDialogCallback {

    private Toolbar mToolbar;
    private CustomTextview mTvTitle;
    private CustomTextview mTvUpdate;

    private EditText mEtEmail;
    private EditText mEtDOB;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;

    private int UPDATION = 1;
    private ProgressDialog pd;

    private static final String DATEPICKER_TAG = "datepicker";
    private Dialog mDialogRespond;
    private String fcm_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initialize();
    }

    private void initialize() {

        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);
        final Calendar calendar = Calendar.getInstance();
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.edit_password));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mTvUpdate = findViewById(R.id.tv_update);
        mEtEmail = findViewById(R.id.et_email);
        mEtDOB = findViewById(R.id.et_dob);
        mEtPassword = findViewById(R.id.et_new_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mEtDOB.setOnClickListener(this);
        mTvUpdate.setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        moveToNextActivityWithFade(true, StartupActivity.class, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_update:
                register();
                break;
            case R.id.et_dob:
                showDateSelector();
                break;

        }
    }

    private void showDateSelector() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this,
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

                                mEtDOB.setText(datetime);
                            } else if (date1.after(date2)) {
                                showAlert(getString(R.string.error), getString(R.string.select_past_date), ResetPasswordActivity.this, null);
                            } else {

                                mEtDOB.setText(datetime);
                            }
                        } catch (ParseException e) {
                            showAlert(getString(R.string.error), getString(R.string.unexpected_error), ResetPasswordActivity.this, null);
                        }
                    }
                }, mYear, mMonth, mDay);
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setTitle(null);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    private void register() {
        Log.d("TAG", "register called");
        if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_email), ResetPasswordActivity.this, mEtEmail);
        } else if (TextUtils.isEmpty(mEtDOB.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_medical), ResetPasswordActivity.this, mEtDOB);
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.empty_Password), ResetPasswordActivity.this, mEtPassword);
        } else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.confirm_password), ResetPasswordActivity.this, mEtConfirmPassword);
        } else if (!mEtConfirmPassword.getText().toString().trim().equals(mEtPassword.getText().toString().trim())) {
            showAlert(getString(R.string.app_name), getString(R.string.password_do_not_match), ResetPasswordActivity.this, mEtConfirmPassword);
        } else {

            if (NetWorkUtil.isNetworkAvailable(ResetPasswordActivity.this)) {

                pd = new ProgressDialog(ResetPasswordActivity.this);
                pd.setMessage("Changing password...");
                pd.show();
                fcm_token = FirebaseInstanceId.getInstance().getToken();
                if (fcm_token == null) {
                    fcm_token = "";
                }

                WebserviceHandler.resetPassword(fcm_token, mEtEmail.getText().toString().trim(), mEtDOB.getText().toString().trim(), mEtPassword.getText().toString().trim(), ResetPasswordActivity.this, UPDATION);
            } else {
                showNetworkAlert();
            }
        }
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

                    CacheUtility.writeCache(ResetPasswordActivity.this, CacheConstants.PROFILE, signInModel);
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(ResetPasswordActivity.this, CacheConstants.PROFILE));
                    moveToNextActivityWithFade(true, HomeActivity.class, null);
                } else {
                    showAlert(getString(R.string.error), response.getMessage(), ResetPasswordActivity.this, null);
                }

            }
        }
    }

    @Override
    public void connectionError(String msg, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        showAlert("Error", msg, ResetPasswordActivity.this, null);
    }

    @Override
    public void onOkClick(View v,int actionid) {
        if (v != null) {
            if (v == mEtDOB) {
                showDateSelector();
            } else {
                v.requestFocus();
            }
        }
    }
}
