package net.appitiza.android.lifedrop.app;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import net.appitiza.android.lifedrop.config.Config;
import net.appitiza.android.lifedrop.model.SignInModel;

public class Bloodbank extends Application {


    private static final String TAG = Bloodbank.class.getSimpleName();
    private static Bloodbank instance;
    private static SignInModel mData;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Config.isProduction)
            initiateLeakCanary();
        loadAppState();
    }

    private void initiateLeakCanary() {

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }


    public void loadAppState() {

        instance = this;
    }

    private Bloodbank getInstance() {
        if (instance == null) {
            instance = this;
        }
        return instance;
    }

    public static void setSignInData(SignInModel data) {
        mData = data;
    }

    public static SignInModel getSignInData() {
        return mData;
    }
}