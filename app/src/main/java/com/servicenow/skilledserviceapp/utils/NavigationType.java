package com.servicenow.skilledserviceapp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link NavigationType} is being used to
 */

public enum NavigationType {
    NAV_LOGIN("nav_login"),
    NAV_SIGNUP("nav_signup"),
    NAV_SKILLS_FRAGMENT("nav_skills_fragment"),
    NAV_REQUEST_TASK("nav_request_task"),
    NAV_REQUEST_TASK_WITH_SKILL("nav_request_task_with_skill");

    private static Map<String, NavigationType> navigationTypeMap;
    private String value = "";

    static {
        navigationTypeMap = new HashMap<>();
        NavigationType[] navigationTypes = values();

        for (NavigationType navigationType : navigationTypes) {
            navigationTypeMap.put(navigationType.getValue(), navigationType);
        }
    }

    private NavigationType(String value) {
        this.value = value;
    }

    public static NavigationType getNavigationType(String navigationValue) {
        return navigationTypeMap.get(navigationValue);
    }

    public String getValue() {
        return this.value;
    }
}
