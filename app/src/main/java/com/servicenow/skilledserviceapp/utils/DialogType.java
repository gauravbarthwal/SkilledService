package com.servicenow.skilledserviceapp.utils;

public enum DialogType {

    DIALOG_FAILURE(Constants.DIALOG_FAILURE),
    DIALOG_SUCCESS(Constants.DIALOG_SUCCESS),
    DIALOG_TASK_ACTION(Constants.DIALOG_TASK_ACTION),
    DIALOG_RATING(Constants.DIALOG_RATING);

    String value = "";
    DialogType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
