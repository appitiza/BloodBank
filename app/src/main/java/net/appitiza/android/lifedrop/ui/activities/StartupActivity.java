package net.appitiza.android.lifedrop.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Tags.FragmentTags;
import net.appitiza.android.lifedrop.ui.callbacks.StartupAction;
import net.appitiza.android.lifedrop.ui.fragments.LoginFragment;
import net.appitiza.android.lifedrop.ui.fragments.RegisterFragment;

public class StartupActivity extends BaseActivity implements StartupAction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        login();
    }

    @Override
    public void register() {
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.REGISTER_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new RegisterFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.REGISTER_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void login() {
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.LOGIN_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new LoginFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.LOGIN_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void load() {
        moveToNextActivityWithFade(false, HomeActivity.class, null);
    }
}
