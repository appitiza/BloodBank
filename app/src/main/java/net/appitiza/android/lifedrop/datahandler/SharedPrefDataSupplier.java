package net.appitiza.android.lifedrop.datahandler;

import android.content.Context;


/**
 * Zco Engineering Dept.
 * Supply the SP data or insert SP data
 * Concrete class for inserting and retrieving the data
 */
public class SharedPrefDataSupplier extends SPDataHandler {

    private static String TAG = "SharedPrefDataSupplier";

    private String mDisplayName = "";
    private String mEmail = "";
    private String mAddress = "";
    private String mImageUrl = "";
    private String mLoginEmail = "";
    private String mLoginPassword = "";
    private int mMinFilterPrice;
    private int mMaxFilterPrice;
    private int mFilterDistance;

    public SharedPrefDataSupplier(Context context) {
        super(context);
    }


    public String getmDisplayName() {
        this.mDisplayName = super.getStringSpData(SPConstantInterface.SP_DISPLAY_NAME, "");
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
        super.saveStringToSp(SPConstantInterface.SP_DISPLAY_NAME, mDisplayName);
    }

    public String getmEmail() {
        this.mEmail = super.getStringSpData(SPConstantInterface.SP_EMAIL, "");
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
        super.saveStringToSp(SPConstantInterface.SP_EMAIL, mEmail);
    }

    public String getmAddress() {
        this.mAddress = super.getStringSpData(SPConstantInterface.SP_ADDRESS, "");
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
        super.saveStringToSp(SPConstantInterface.SP_ADDRESS, mAddress);
    }

    public String getmImageUrl() {
        mImageUrl = super.getStringSpData(SPConstantInterface.SP_IMAGE_URL, "");
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
        super.saveStringToSp(SPConstantInterface.SP_IMAGE_URL, mImageUrl);
    }

    public String getmLoginEmail() {

        mLoginEmail = super.getStringSpData(SPConstantInterface.SP_LOGIN_EMAIL, "");
        return mLoginEmail;
    }

    public void setmLoginEmail(String mLoginEmail) {
        super.saveStringToSp(SPConstantInterface.SP_LOGIN_EMAIL, mLoginEmail);
    }

    public String getmLoginPassword() {
        mLoginPassword = super.getStringSpData(SPConstantInterface.SP_LOGIN_PASSWORD, "");
        return mLoginPassword;
    }

    public void setmLoginPassword(String mLoginPassword) {
        super.saveStringToSp(SPConstantInterface.SP_LOGIN_PASSWORD, mLoginPassword);
    }

    public int getmMinFilterPrice() {
        mMinFilterPrice = super.getIntegerSpData(SPConstantInterface.SP_MIN_FILTER_PRICE, 0);
        return mMinFilterPrice;
    }

    public void setmMinFilterPrice(int mFilterPrice) {
        this.mMinFilterPrice = mFilterPrice;
        super.setIntegerData(SPConstantInterface.SP_MIN_FILTER_PRICE, mFilterPrice);
    }

    public int getmMaxFilterPrice() {
        mMaxFilterPrice = super.getIntegerSpData(SPConstantInterface.SP_MAX_FILTER_PRICE, 0);
        return mMaxFilterPrice;
    }

    public void setmMaxFilterPrice(int mFilterPrice) {
        this.mMaxFilterPrice = mFilterPrice;
        super.setIntegerData(SPConstantInterface.SP_MAX_FILTER_PRICE, mFilterPrice);
    }

    public int getmFilterDistance() {
        mFilterDistance = super.getIntegerSpData(SPConstantInterface.SP_FILTER_DISTANCE, 0);
        return mFilterDistance;
    }

    public void setmFilterDistance(int mFilterDistance) {
        this.mFilterDistance = mFilterDistance;
        super.setIntegerData(SPConstantInterface.SP_FILTER_DISTANCE, mFilterDistance);
    }

    private boolean nullChecker(String key) {
        return key != null && key.length() > 0 && !key.equalsIgnoreCase("null");
    }
}
