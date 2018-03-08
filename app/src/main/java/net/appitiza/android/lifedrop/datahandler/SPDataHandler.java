package net.appitiza.android.lifedrop.datahandler;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class SPDataHandler {

    private Context mContext;
    private SharedPreferences mPreference;

    protected SPDataHandler(Context context) {
        this.mContext = context;
        this.mPreference = mContext.getSharedPreferences(SPConstantInterface.SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * COMMON SETTER FOR STRING SP DATA
     */
    protected void saveStringToSp(String key, String value) {

        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(key, value);
        editor.apply();
    }


    /**
     * COMMON SETTER FOR BOOLEAN SP DATA
     */
    protected void saveBooleanToSp(String key, boolean value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * COMMON GETTER FOR STRING SP DATA
     */
    protected String getStringSpData(String key, String defaultVaule) {
        return this.mPreference.getString(key, defaultVaule);
    }


    /**
     * COMMON GETTER FOR BOOLEAN SP DATA
     */
    protected boolean getBooleanSpData(String key, boolean defaultValue) {
        return this.mPreference.getBoolean(key, defaultValue);
    }


    /**
     * COMMON GETTER FOR INTEGER SP DATA
     */
    protected int getIntegerSpData(String key, int defaultValue) {

        return this.mPreference.getInt(key, defaultValue);
    }

    /**
     * COMMON SETTER FOR INTEGER DATA
     */
    protected void setIntegerData(String key, int value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    protected void removeSpData(String key) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.remove(key).apply();
    }

}
