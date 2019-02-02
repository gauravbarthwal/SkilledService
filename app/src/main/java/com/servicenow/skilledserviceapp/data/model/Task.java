package com.servicenow.skilledserviceapp.data.model;

public class Task {
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskRequiredSkill() {
        return taskRequiredSkill;
    }

    public void setTaskRequiredSkill(String taskRequiredSkill) {
        this.taskRequiredSkill = taskRequiredSkill;
    }

    public String getTaskFrom() {
        return taskFrom;
    }

    public void setTaskFrom(String taskFrom) {
        this.taskFrom = taskFrom;
    }

    public String getTaskTo() {
        return taskTo;
    }

    public void setTaskTo(String taskTo) {
        this.taskTo = taskTo;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public float getTaskRatings() {
        return taskRatings;
    }

    public void setTaskRatings(float taskRatings) {
        this.taskRatings = taskRatings;
    }

    private int taskId;
    private String taskDescription;
    private String taskRequiredSkill;
    private String taskFrom;
    private String taskTo;
    private String taskStatus;
    private float taskRatings;

}
