package net.appitiza.android.lifedrop.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.appitiza.android.lifedrop.R;
import net.appitiza.android.lifedrop.Tags.FragmentTags;
import net.appitiza.android.lifedrop.Views.CustomTextview;
import net.appitiza.android.lifedrop.app.Bloodbank;
import net.appitiza.android.lifedrop.cache.CacheConstants;
import net.appitiza.android.lifedrop.cache.CacheUtility;
import net.appitiza.android.lifedrop.datahandler.SharedPrefDataSupplier;
import net.appitiza.android.lifedrop.observer.ObserverActions;
import net.appitiza.android.lifedrop.observer.ObserverData;
import net.appitiza.android.lifedrop.observer.RepositoryObserver;
import net.appitiza.android.lifedrop.observer.UserDataRepository;
import net.appitiza.android.lifedrop.ui.callbacks.HomeActivityActions;
import net.appitiza.android.lifedrop.ui.fragments.MapsFragment;
import net.appitiza.android.lifedrop.ui.fragments.MessageFragment;
import net.appitiza.android.lifedrop.ui.fragments.NewsFragment;
import net.appitiza.android.lifedrop.ui.fragments.RequestFragment;
import net.appitiza.android.lifedrop.ui.fragments.SearchFragment;
import net.appitiza.android.lifedrop.utils.HomeButtonZoomInterpoletor;
import net.appitiza.android.lifedrop.utils.NetWorkUtil;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class HomeActivity extends BaseActivity implements View.OnClickListener, HomeActivityActions, RepositoryObserver {

    private FloatingActionButton mFab;
    private FloatingActionButton mFabSearch;
    private FloatingActionButton mFabMap;
    private FrameLayout mFlFab;
    private RelativeLayout mRlFooter;
    private LinearLayout mLlOption;
    private RelativeLayout mRlOption;

    private ImageView mIvRequest;
    private ImageView mIvNews;
    private ImageView mIvMessage;
    private ImageView mIvProfileInfo;


    private CustomTextview mTvRequest;
    private CustomTextview mTvNews;
    private CustomTextview mTvMessage;
    private CustomTextview mTvProfileInfo;

    private LinearLayout mLlRequest;
    private LinearLayout mLlNews;
    private LinearLayout mLlMessage;
    private LinearLayout mLlProfileInfo;


    private Toolbar mToolbar;
    private CustomTextview mTvTitle;
    private CustomTextview mTvName;

    private Animation mButtonZoom;
    private HomeButtonZoomInterpoletor interpolator;

    private SharedPrefDataSupplier mDataSupplier;
    private ObserverActions mUserDataRepository;
    private Dialog mDialogRespond;
    private Fragment mapsfragment;
    View ivMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();
        setClick();
        loadRequest();
        tutorial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.password:
                moveToNextActivity(UpdatePasswordActivity.class, null, mIvProfileInfo, getString(R.string.transition_update));
                return true;
            case R.id.share:
                shareApp();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        CacheUtility.clearCache(this, CacheConstants.PROFILE);
        moveToNextActivityWithFade(true, StartupActivity.class, null);

    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Link To instal App");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.app_name)));

    }

    private void initialize() {
        mUserDataRepository = UserDataRepository.getInstance();
        mUserDataRepository.registerObserver(HomeActivity.this);

        mDataSupplier = new SharedPrefDataSupplier(this);
        mFab = findViewById(R.id.fab);

        mFabSearch = findViewById(R.id.fabSearch);
        mFabMap = findViewById(R.id.fabMap);

        mFlFab = findViewById(R.id.fl_fab);
        mRlFooter = findViewById(R.id.rl_footer);
        mRlOption = findViewById(R.id.rl_option);
        mLlOption = findViewById(R.id.ll_option);


        mIvRequest = findViewById(R.id.iv_request);
        mIvNews = findViewById(R.id.iv_news);
        mIvMessage = findViewById(R.id.iv_message);
        mIvProfileInfo = findViewById(R.id.iv_profile);


        mTvRequest = findViewById(R.id.tv_request);
        mTvNews = findViewById(R.id.tv_news);
        mTvMessage = findViewById(R.id.tv_message);
        mTvProfileInfo = findViewById(R.id.tv_profile);

        mLlRequest = findViewById(R.id.ll_request);
        mLlNews = findViewById(R.id.ll_news);
        mLlMessage = findViewById(R.id.ll_message);
        mLlProfileInfo = findViewById(R.id.ll_profile);


        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);
        mTvName = findViewById(R.id.tv_name);
        if (Bloodbank.getSignInData() != null)
            mTvName.setText(Bloodbank.getSignInData().getFirstName());

        setSupportActionBar(mToolbar);


        mButtonZoom = AnimationUtils.loadAnimation(this, R.anim.home_button_zoom);
        interpolator = new HomeButtonZoomInterpoletor(0.2, 20);
        mButtonZoom.setInterpolator(interpolator);

        mFab.startAnimation(mButtonZoom);
    }

    private void setClick() {
        mFab.setOnClickListener(this);
        mFabSearch.setOnClickListener(this);
        mFabMap.setOnClickListener(this);
        mLlRequest.setOnClickListener(this);
        mLlNews.setOnClickListener(this);
        mLlMessage.setOnClickListener(this);
        mLlProfileInfo.setOnClickListener(this);
        mRlOption.setOnClickListener(this);
        mTvName.setOnClickListener(this);


    }

    private void clearSelection() {
        mLlOption.setVisibility(View.GONE);
        mRlOption.setVisibility(View.GONE);

        mIvRequest.setImageResource(R.drawable.request);
        mIvNews.setImageResource(R.drawable.news);
        mIvMessage.setImageResource(R.drawable.envelope);
        mIvProfileInfo.setImageResource(R.drawable.profileinfo);

        mTvNews.setTypeface(null, Typeface.NORMAL);
        mTvRequest.setTypeface(null, Typeface.NORMAL);
        mTvMessage.setTypeface(null, Typeface.NORMAL);
        mTvProfileInfo.setTypeface(null, Typeface.NORMAL);

        mTvNews.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.text_color));
        mTvRequest.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.text_color));
        mTvMessage.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.text_color));
        mTvProfileInfo.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.text_color));
    }

    private void loadRequest() {
        clearSelection();
        mTvRequest.setTypeface(null, Typeface.BOLD);
        mTvRequest.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        mIvRequest.setImageResource(R.drawable.coins_pressed);
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.REQUEST_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new RequestFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.REQUEST_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.request));
        }
    }

    private void loadNews() {
        clearSelection();
        mTvNews.setTypeface(null, Typeface.BOLD);
        mTvNews.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        mIvNews.setImageResource(R.drawable.news_pressed);
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.NEWS_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new NewsFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.NEWS_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.news));
        }
    }

    private void loadMap() {
        clearSelection();
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.MAP_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new MapsFragment();
        }
        mapsfragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.MAP_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.map));
        }
    }

    private void loadSearch() {
        clearSelection();
        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.SEARCH_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new SearchFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.SEARCH_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.search));
        }
    }

    private void loadMessage() {
        clearSelection();
        mTvMessage.setTypeface(null, Typeface.BOLD);
        mTvMessage.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        mIvMessage.setImageResource(R.drawable.envelope_pressed);

        FragmentManager mFragManager = getSupportFragmentManager();
        Fragment fragment = mFragManager.findFragmentByTag(FragmentTags.MESSAGE_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new MessageFragment();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment, FragmentTags.MESSAGE_FRAGMENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
        if (getSupportActionBar() != null) {
            mTvTitle.setText(getString(R.string.messages));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mLlOption.getVisibility() == View.VISIBLE) {
                    mButtonZoom.cancel();
                    mLlOption.setVisibility(View.GONE);
                    mRlOption.setVisibility(View.GONE);

                } else {
                    mLlOption.setVisibility(View.VISIBLE);
                    mRlOption.setVisibility(View.VISIBLE);

                    mButtonZoom.setInterpolator(interpolator);
                    mLlOption.startAnimation(mButtonZoom);
                }
                break;
            case R.id.fabMap:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    loadMap();
                    //moveToNextActivity(MainActivity.class, null, mIvProfileInfo, getString(R.string.transition_update));
                } else {
                    showNetworkAlert();
                }

                break;
            case R.id.fabSearch:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    loadSearch();
                } else {
                    showNetworkAlert();
                }

                break;
            case R.id.rl_option:
                mButtonZoom.cancel();
                mLlOption.setVisibility(View.GONE);
                mRlOption.setVisibility(View.GONE);
                break;
            case R.id.ll_request:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    loadRequest();
                } else {
                    showNetworkAlert();
                }


                break;
            case R.id.ll_news:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    loadNews();
                } else {
                    showNetworkAlert();
                }
                break;
            case R.id.ll_message:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    loadMessage();
                } else {
                    showNetworkAlert();
                }

                break;
            case R.id.ll_profile:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    moveToNextActivity(UpdateActivity.class, null, mIvProfileInfo, getString(R.string.transition_update));
                } else {
                    showNetworkAlert();
                }
                break;
            case R.id.tv_name:
                if (NetWorkUtil.isNetworkAvailable(HomeActivity.this)) {
                    moveToNextActivity(UpdateActivity.class, null, mIvProfileInfo, getString(R.string.transition_update));
                }
                break;
        }
    }

    private void showExitWarning() {
        final AlertDialog mAlert = new AlertDialog.Builder(this).create();
        mAlert.setTitle(getString(R.string.exit));
        mAlert.setMessage(getString(R.string.exit_message));
        mAlert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mDataSupplier.setmFilterDistance(0);
                mDataSupplier.setmMinFilterPrice(0);
                mAlert.dismiss();
                finish();
            }
        });
        mAlert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAlert.dismiss();
            }
        });
        mAlert.show();

    }

    @Override
    public void onBackPressed() {
        showExitWarning();
    }

    @Override
    public void listScrolling(Boolean isScrolling) {
        if (isScrolling) {
            mRlFooter.animate()
                    .translationY(mRlFooter.getHeight())
                    .setInterpolator(new AnticipateInterpolator(2))
                    .start();
            mFlFab.animate()
                    .translationY(mFlFab.getHeight())
                    .setInterpolator(new AnticipateInterpolator(4))
                    .start();

        } else {
            mRlFooter.animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(2))
                    .start();
            mFlFab.animate()
                    .translationY(0)
                    .setInterpolator(new AccelerateInterpolator(2))
                    .start();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserDataRepository.removeObserver(this);
    }

    @Override
    public void onUserDataChanged(ObserverData obj) {
        if (Bloodbank.getSignInData() != null)
            mTvName.setText(Bloodbank.getSignInData().getFirstName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MapsFragment.LOCATION_SETTINGS) {
            mapsfragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void tutorial() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        config.setMaskColor(ContextCompat.getColor(HomeActivity.this, R.color.tutorial_bg));
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1");
        sequence.setConfig(config);
        sequence.addSequenceItem(mFlFab,
                "Click here to search and view around", "GOT IT");
        sequence.addSequenceItem(mLlProfileInfo,
                "Click here to update your profile", "GOT IT");
        sequence.addSequenceItem(mLlNews,
                "Click here to get latest medical news", "GOT IT");
        sequence.addSequenceItem(mLlMessage,
                "Click here to get posted messages", "GOT IT");
        sequence.addSequenceItem(mLlRequest,
                "Click here to request for blood", "GOT IT");


        sequence.start();
    }
}
