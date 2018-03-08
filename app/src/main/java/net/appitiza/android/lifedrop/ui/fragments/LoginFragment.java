package net.appitiza.android.lifedrop.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.ui.activities.HomeActivity;
import net.appitiza.android.lifedrop.ui.activities.ResetPasswordActivity;
import net.appitiza.android.lifedrop.ui.callbacks.StartupAction;
import net.appitiza.android.lifedrop.ui.callbacks.ValidationDialogCallback;
import net.appitiza.android.lifedrop.utils.GeneralUtils;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.lang.ref.WeakReference;


public class LoginFragment extends BaseFragment implements View.OnClickListener, WebserviceCallBack, ValidationDialogCallback {
    private WeakReference<Context> mWeakActivity;
    private CustomTextview mTvRegister;
    private CustomTextview mTvLogin;
    private CustomTextview mTvForgotPassword;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private int LOGIN = 1;
    private ProgressDialog pd;
    private String fcm_token = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        initialize(v);
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
        fcm_token = FirebaseInstanceId.getInstance().getToken();
        mTvRegister = v.findViewById(R.id.tv_register);
        mTvLogin = v.findViewById(R.id.tv_login);
        mTvForgotPassword = v.findViewById(R.id.tv_forgot);
        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
        mTvRegister.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        mTvForgotPassword.setOnClickListener(this);
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
                ((StartupAction) mWeakActivity.get()).register();
                break;
            case R.id.tv_login:
                validate();
                break;
            case R.id.tv_forgot:
                Intent intent = new Intent(mWeakActivity.get(), ResetPasswordActivity.class);
                startActivity(intent);
                ((Activity)mWeakActivity.get()).finish();
                break;
        }
    }


    private void validate() {
        if (TextUtils.isEmpty(mEtEmail.getText().toString().trim())) {
            showAlert("Field Empty", "Enter a valid email",LoginFragment.this,mEtEmail);
        } else if (!GeneralUtils.isValidEmail(mEtEmail.getText().toString().trim())) {
            showAlert("Field Empty", "Enter a valid email");
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            showAlert("Field Empty", "Enter a password",LoginFragment.this,mEtPassword);
        } else {

            if (NetWorkUtil.isNetworkAvailable(getContext())) {
                pd = new ProgressDialog(mWeakActivity.get());
                pd.setMessage("Signing In...");
                pd.show();
                if (fcm_token == null) {
                    fcm_token = "";
                }
                WebserviceHandler.signIn(fcm_token, mEtEmail.getText().toString().trim(), mEtPassword.getText().toString().trim(), LoginFragment.this, LOGIN);
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
        if (requestCode == LOGIN && response != null) {

            if (response.getErrorcode() == 0) {
                SignInModel signInModel = ResponseParser.parseSignIn(response);
                if (signInModel != null) {
                    CacheUtility.writeCache(mWeakActivity.get(), CacheConstants.PROFILE, signInModel);
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(mWeakActivity.get(), CacheConstants.PROFILE));
                    moveToNextActivity(true, HomeActivity.class, null);

                } else {
                    showAlert(getString(R.string.error), getString(R.string.unexpected_error));
                }

            } else {
                showAlert(getString(R.string.error), response.getMessage());
            }
        } else {
            showAlert(getString(R.string.error), getString(R.string.unexpected_error));
        }
    }

    @Override
    public void connectionError(String msg, int requestCode) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        showAlert(getString(R.string.error), getString(R.string.unexpected_error));
    }

    @Override
    public void onOkClick(View v,int actionid) {
        if (v != null) {
            v.requestFocus();
        }
    }
}
