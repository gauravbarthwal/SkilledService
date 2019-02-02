package com.servicenow.skilledserviceapp.data.model;

/**
 * model to store skill data
 */
public class Skill {
    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    private int skillId;
    private String skillType;
}
