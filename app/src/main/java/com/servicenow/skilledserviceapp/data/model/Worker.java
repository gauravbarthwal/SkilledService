package com.servicenow.skilledserviceapp.data.model;

import java.util.ArrayList;

public class Worker {
    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public float getWorkerRatings() {
        return workerRatings;
    }

    public void setWorkerRatings(float workerRatings) {
        this.workerRatings = workerRatings;
    }

    public ArrayList<Task> getmAssociatedTasks() {
        return mAssociatedTasks;
    }

    public void setmAssociatedTasks(ArrayList<Task> mAssociatedTasks) {
        this.mAssociatedTasks = mAssociatedTasks;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    private String workerId;
    private int skillId;
    private String workerName;
    private String skillName;
    private float workerRatings;
    private ArrayList<Task> mAssociatedTasks = new ArrayList<>();
}
