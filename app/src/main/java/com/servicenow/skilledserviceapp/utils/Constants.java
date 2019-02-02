package com.servicenow.skilledserviceapp.utils;

import com.servicenow.skilledserviceapp.R;

public class Constants {
    public static final boolean PRINT_LOGS = true;

    public static final String USER_REQUESTER = "Requester";
    public static final String USER_WORKER = "Worker";

    /**
     * array is being used to load icons
     */
    public static final int[] skillIconsArray = new int[]{
            R.drawable.ic_barber,
            R.drawable.ic_beautician,
            R.drawable.ic_carpenter,
            R.drawable.ic_cooker,
            R.drawable.ic_electrician,
            R.drawable.ic_house_cleaning,
            R.drawable.ic_movers,
            R.drawable.ic_painter,
            R.drawable.ic_plumber,
            R.drawable.ic_seamstrees,
            R.drawable.ic_stylist
    };

    /**
     * Preference keys
     */
    static final String PREF_NAME = "servicenow_preference";
    public static final String PREF_KEY_LOGGED_IN_USER_ID = "pref_key_logged_in_user_id";
    public static final String PREF_KEY_IS_REQUESTER = "pref_key_is_requester";

    /**
     * keys to send data between Activity and Fragments
     */
    public static final String KEY_LOAD_FRAGMENT = "key_load_fragment";
    public static final String KEY_DIALOG_MAP = "key_dialog_map";
    public static final String KEY_DIALOG_TYPE = "key_dialog_type";

    public static final String DIALOG_FAILURE = "dialog_failure";
    public static final String DIALOG_KEY_MESSAGE = "dialog_key_message";
}
