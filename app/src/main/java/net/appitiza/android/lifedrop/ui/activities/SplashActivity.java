package net.appitiza.android.lifedrop.ui.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.model.SignInModel;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;
import net.appitiza.android.lifedrop.webservices.BaseResponseModel;
import net.appitiza.android.lifedrop.webservices.ResponseParser;
import net.appitiza.android.lifedrop.webservices.WebserviceCallBack;
import net.appitiza.android.lifedrop.webservices.WebserviceHandler;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity implements WebserviceCallBack {
    private Handler mHandler;
    private DelayRunnable mRunnable;

    private int LOGIN = 1;
    private String fcm_token = "";
    private WeakReference<Context> mWeakActivity;


   private static class DelayRunnable implements Runnable {

       WeakReference<SplashActivity> mWeak;
       private DelayRunnable(SplashActivity activity){
           mWeak = new WeakReference<>(activity);
       }
       @Override
       public void run() {
           mWeak.get().moveToNextActivityWithFade(true, StartupActivity.class, null);
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mWeakActivity = new WeakReference<Context>(SplashActivity.this);
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        mHandler  = new Handler();
        mRunnable = new DelayRunnable(this);
        autoLogin();

    }

    private void autoLogin() {
        fcm_token = FirebaseInstanceId.getInstance().getToken();
        Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(mWeakActivity.get(), CacheConstants.PROFILE));
        if (Bloodbank.getSignInData() != null && Bloodbank.getSignInData().getEmail() != null && Bloodbank.getSignInData().getPassword() != null) {
            String email = Bloodbank.getSignInData().getEmail();
            String password = Bloodbank.getSignInData().getPassword();
            if (!email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {

                validate(email, password);

            } else {
                movetoStartUp();
            }
        } else {
            movetoStartUp();
        }
    }

    private void validate(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            if (NetWorkUtil.isNetworkAvailable(mWeakActivity.get())) {
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                if(fcm_token == null)
                {
                    fcm_token = "";
                }
                WebserviceHandler.signIn(fcm_token, email, password, (WebserviceCallBack) mWeakActivity.get(), LOGIN);
            } else {
                movetoStartUp();
            }
        } else {

            movetoStartUp();
        }

    }

    private void movetoStartUp() {

        /*mRunnable = new Runnable() {
            @Override
            public void run() {
                moveToNextActivityWithFade(true, StartupActivity.class, null);
            }
        };*/

        mHandler.postDelayed(mRunnable, 4000);
    }

    @Override
    public void connectionSuccess(BaseResponseModel response, int requestCode) {
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        if (requestCode == LOGIN && response != null && response.getErrorcode() == 0) {
            SignInModel signInModel = ResponseParser.parseSignIn(response);
            if (signInModel != null) {
                CacheUtility.writeCache(mWeakActivity.get(), CacheConstants.PROFILE, signInModel);
                Bloodbank.setSignInData((SignInModel) CacheUtility.readCache(mWeakActivity.get(), CacheConstants.PROFILE));

               /* mHandler = new Handler();

                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        moveToNextActivityWithFade(true, HomeActivity.class, null);
                    }
                };*/
                mHandler.postDelayed(mRunnable, 2000);


            } else {
               /* mHandler = new Handler();

                Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        moveToNextActivityWithFade(true, StartupActivity.class, null);
                    }
                };*/
                mHandler.postDelayed(mRunnable, 2000);

            }

        }
        else
        {
           /* mHandler = new Handler();

            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    moveToNextActivityWithFade(true, StartupActivity.class, null);
                }
            };*/
            mHandler.postDelayed(mRunnable, 2000);
        }
    }

    @Override
    public void connectionError(String msg, int requestCode) {
        findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        moveToNextActivityWithFade(true, StartupActivity.class, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }
}
