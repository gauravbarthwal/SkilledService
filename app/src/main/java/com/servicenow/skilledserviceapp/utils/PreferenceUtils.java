package com.servicenow.skilledserviceapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class to save data into preferences
 */
public class PreferenceUtils {
    private static PreferenceUtils mPreferenceUtils;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private PreferenceUtils(Context mContext) {
        this.mContext = mContext;
        this.mSharedPreferences = mContext.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mSharedPreferences.edit();
    }

    public static PreferenceUtils getInstance(Context mContext) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new PreferenceUtils(mContext);
        }

        return mPreferenceUtils;
    }

    /*
     * GENERIC PREFERENCES
     */

    /**
     * sets String preferences
     * @param preferenceKey - key of preference
     * @param preferenceValue - value of preference
     */
    public void setStringPreference(String preferenceKey, String preferenceValue) {
        mEditor.putString(preferenceKey, preferenceValue).commit();
    }

    /**
     * returns string preference
     * @param preferenceKey - key of preference
     * @return - {@link String}
     */
    public String getStringPreference(String preferenceKey) {
        return mSharedPreferences.getString(preferenceKey, "");
    }

    /**
     * sets Boolean preference
     * @param preferenceKey - preference key
     * @param preferenceValue- preference value
     */
    public void setBooleanPreference(String preferenceKey, Boolean preferenceValue) {
        mEditor.putBoolean(preferenceKey, preferenceValue).commit();
    }

    /**
     * returns boolean preference
     * @param preferenceKey - preference key
     * @return - {@link Boolean}
     */
    public Boolean getBooleanPreference(String preferenceKey) {
        return mSharedPreferences.getBoolean(preferenceKey, false);
    }

    /**
     * sets integer preferences
     * @param preferenceKey - key of preference
     * @param preferenceValue - value of preference
     */
    public void setIntPreference(String preferenceKey, int preferenceValue) {
        mEditor.putInt(preferenceKey, preferenceValue).commit();
    }

    /**
     * returns int preference
     * @param preferenceKey - key of preference
     * @return - int
     */
    public int getIntPreference(String preferenceKey) {
        return mSharedPreferences.getInt(preferenceKey, -1);
    }

    /**
     * sets float preferences
     * @param preferenceKey - key of preference
     * @param preferenceValue - value of preference
     */
    public void setFloatPreference(String preferenceKey, float preferenceValue) {
        mEditor.putFloat(preferenceKey, preferenceValue).commit();
    }

    /**
     * returns float preference
     * @param preferenceKey - key of preference
     * @return - float
     */
    public float getFloatPreference(String preferenceKey) {
        return mSharedPreferences.getFloat(preferenceKey, 0);
    }
}