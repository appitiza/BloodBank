package net.appitiza.android.lifedrop.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

public class UpdatePasswordActivity extends BaseActivity implements View.OnClickListener, WebserviceCallBack {

    private Toolbar mToolbar;
    private CustomTextview mTvTitle;
    private CustomTextview mTvName;
    private CustomTextview mTvUpdate;

    private EditText mEtOldPassword;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;

    private int UPDATION = 1;
    private Animation animShake;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        initialize();
    }

    private void initialize() {

        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.edit_password));
            mTvName = findViewById(R.id.tv_name);
            mTvName.setText(Bloodbank.getSignInData().getFirstName());
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        animShake = AnimationUtils.loadAnimation(UpdatePasswordActivity.this, R.anim.shake);

        mTvUpdate = findViewById(R.id.tv_update);
        mEtOldPassword = findViewById(R.id.et_old_password);
        mEtPassword = findViewById(R.id.et_new_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTvUpdate.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_update:
                register();
                break;


        }
    }


    private void register() {
        Log.d("TAG", "register called");
        clearValidation();
        if (TextUtils.isEmpty(mEtOldPassword.getText().toString().trim())) {
            mEtOldPassword.setBackgroundResource(R.drawable.border_et_empty);
            mEtOldPassword.startAnimation(animShake);
            showAlert(getString(R.string.app_name), getString(R.string.old_Password));
        } else if (TextUtils.isEmpty(mEtPassword.getText().toString().trim())) {
            mEtPassword.setBackgroundResource(R.drawable.border_et_empty);
            mEtPassword.startAnimation(animShake);
            showAlert(getString(R.string.app_name), getString(R.string.empty_Password));
        } else if (TextUtils.isEmpty(mEtConfirmPassword.getText().toString().trim())) {
            mEtConfirmPassword.setBackgroundResource(R.drawable.border_et_empty);
            mEtConfirmPassword.startAnimation(animShake);
            showAlert(getString(R.string.app_name), getString(R.string.confirm_password));
        } else if (!mEtConfirmPassword.getText().toString().trim().equals(mEtPassword.getText().toString().trim())) {
            mEtConfirmPassword.setBackgroundResource(R.drawable.border_et_empty);
            mEtPassword.setBackgroundResource(R.drawable.border_et_empty);
            showAlert(getString(R.string.app_name), getString(R.string.password_do_not_match));
            mEtPassword.startAnimation(animShake);
            mEtConfirmPassword.startAnimation(animShake);
        } else {

            if (NetWorkUtil.isNetworkAvailable(UpdatePasswordActivity.this)) {

                pd = new ProgressDialog(UpdatePasswordActivity.this);
                pd.setMessage("Changing password...");
                pd.show();
                String fcm_token = FirebaseInstanceId.getInstance().getToken();
                if (fcm_token == null) {
                    fcm_token = "";
                }

                if (Bloodbank.getSignInData() != null) {
                    WebserviceHandler.updatePassword(fcm_token, Bloodbank.getSignInData().getEmail(),mEtOldPassword.getText().toString().trim(), mEtPassword.getText().toString().trim(), UpdatePasswordActivity.this, UPDATION);
                }
            } else {
                showNetworkAlert();
            }

        }
    }

    private void clearValidation() {
        mEtPassword.setBackgroundResource(R.drawable.et_bg);
        mEtConfirmPassword.setBackgroundResource(R.drawable.et_bg);
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
                    CacheUtility.writeCache(UpdatePasswordActivity.this, CacheConstants.PROFILE, signInModel);
                    Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(UpdatePasswordActivity.this, CacheConstants.PROFILE));
                    showAlert(getString(R.string.success), getString(R.string.profile_updated));
                    onBackPressed();
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
        showAlert("Error", msg);
    }

}
