package com.servicenow.skilledserviceapp.utils;

import com.servicenow.skilledserviceapp.R;

public class Constants {
    public static final boolean PRINT_LOGS = true;

    public static final String USER_REQUESTER = "Requester";
    public static final String USER_WORKER = "Worker";

    /**
     * method to get Skill image
     * @param skillName - Skill name
     * @return - Image id
     */
    public static int getSkillIcon(String skillName) {
        if (skillName != null && !skillName.isEmpty()) {
            if (skillName.equalsIgnoreCase("Barber")) {
                return R.drawable.ic_barber;
            } else if (skillName.equalsIgnoreCase("Beautician")) {
                return R.drawable.ic_beautician;
            } else if (skillName.equalsIgnoreCase("Carpenter")) {
                return R.drawable.ic_carpenter;
            } else if (skillName.equalsIgnoreCase("Chef")) {
                return R.drawable.ic_cooker;
            } else if (skillName.equalsIgnoreCase("Electrician")) {
                return R.drawable.ic_electrician;
            } else if (skillName.equalsIgnoreCase("House Cleaning")) {
                return R.drawable.ic_house_cleaning;
            } else if (skillName.equalsIgnoreCase("Movers")) {
                return R.drawable.ic_movers;
            } else if (skillName.equalsIgnoreCase("Painter")) {
                return R.drawable.ic_painter;
            } else if (skillName.equalsIgnoreCase("Plumber")) {
                return R.drawable.ic_plumber;
            } else if (skillName.equalsIgnoreCase("Seamstress")) {
                return R.drawable.ic_seamstrees;
            } else if (skillName.equalsIgnoreCase("Stylist")) {
                return R.drawable.ic_stylist;
            }
            return R.drawable.ic_home_black_24dp;
        } return R.drawable.ic_home_black_24dp;
    }

    /**
     * Preference keys
     */
    static final String PREF_NAME = "servicenow_preference";
    public static final String PREF_KEY_LOGGED_IN_USER_ID = "pref_key_logged_in_user_id";
    public static final String PREF_KEY_IS_REQUESTER = "pref_key_is_requester";
    public static final String PREF_KEY_RATINGS = "pref_key_ratings";

    /**
     * keys to send data between Activity and Fragments
     */
    public static final String KEY_LOAD_FRAGMENT = "key_load_fragment";
    public static final String KEY_DIALOG_MAP = "key_dialog_map";
    public static final String KEY_DIALOG_TYPE = "key_dialog_type";
    public static final String KEY_SKILL_ID = "key_skill_id";

    public static final String DIALOG_FAILURE = "dialog_failure";
    public static final String DIALOG_SUCCESS = "dialog_success";
    public static final String DIALOG_TASK_ACTION = "dialog_task_action";
    public static final String DIALOG_RATING = "dialog_rating";
    public static final String DIALOG_KEY_MESSAGE = "dialog_key_message";
    public static final String DIALOG_KEY_ACTION_LEFT_LABEL = "dialog_key_action_left_label";
    public static final String DIALOG_KEY_ACTION_RIGHT_LABEL = "dialog_key_action_right_label";
}
