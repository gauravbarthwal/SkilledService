package com.servicenow.skilledserviceapp.data.model;

public enum TaskType {
    TASK_PENDING("task_pending"), TASK_ONGOING("task_ongoing"), TASK_COMPLETED("task_completed");
    String value = "";
    TaskType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
