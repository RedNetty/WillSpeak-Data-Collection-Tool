package com.rednetty.voicerecorder.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a user's speech profile including information about
 * speech characteristics and demographic data.
 */
public class UserProfile {
    private String userId;
    private String name;
    private String speechType;
    private String speechImpairmentDetails;
    private String gender;
    private String ageGroup;
    private String nativeLanguage;
    private String deviceInfo;
    private Date profileCreated;
    private Map<String, String> additionalInfo;

    public UserProfile() {
        this.userId = UUID.randomUUID().toString();
        this.profileCreated = new Date();
        this.speechType = "clear";
        this.additionalInfo = new HashMap<>();
        this.deviceInfo = System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpeechType() {
        return speechType;
    }

    public void setSpeechType(String speechType) {
        this.speechType = speechType;
    }

    public String getSpeechImpairmentDetails() {
        return speechImpairmentDetails;
    }

    public void setSpeechImpairmentDetails(String speechImpairmentDetails) {
        this.speechImpairmentDetails = speechImpairmentDetails;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getNativeLanguage() {
        return nativeLanguage;
    }

    public void setNativeLanguage(String nativeLanguage) {
        this.nativeLanguage = nativeLanguage;
    }

    public Date getProfileCreated() {
        return profileCreated;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void addAdditionalInfo(String key, String value) {
        this.additionalInfo.put(key, value);
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }
}