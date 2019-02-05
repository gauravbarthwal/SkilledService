package com.servicenow.skilledserviceapp.utils;

public enum DialogType {

    DIALOG_FAILURE(Constants.DIALOG_FAILURE),
    DIALOG_SUCCESS(Constants.DIALOG_SUCCESS);

    String value = "";
    DialogType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
