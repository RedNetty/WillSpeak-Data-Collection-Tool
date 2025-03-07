package com.rednetty.voicerecorder.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stores metadata about a voice recording.
 * Updated to allow setting the recording ID.
 */
public class RecordingMetadata {
    private String recordingId;
    private String userId;
    private String phraseId;
    private String phraseText;
    private String phraseCategory;
    private String recordingFile;
    private String timestamp;
    private String speechType;
    private String speechImpairmentDetails;
    private String deviceInfo;
    private Map<String, Object> audioProperties;
    private Map<String, String> additionalInfo;

    public RecordingMetadata(String userId, String phraseId, String phraseText,
                             String phraseCategory, String recordingFile, String timestamp, String speechType,
                             String speechImpairmentDetails, String deviceInfo) {
        this.recordingId = UUID.randomUUID().toString();
        this.userId = userId;
        this.phraseId = phraseId;
        this.phraseText = phraseText;
        this.phraseCategory = phraseCategory;
        this.recordingFile = recordingFile;
        this.timestamp = timestamp;
        this.speechType = speechType;
        this.speechImpairmentDetails = speechImpairmentDetails;
        this.deviceInfo = deviceInfo;
        this.audioProperties = new HashMap<>();
        this.additionalInfo = new HashMap<>();
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhraseId() {
        return phraseId;
    }

    public String getPhraseText() {
        return phraseText;
    }

    public String getPhraseCategory() {
        return phraseCategory;
    }

    public String getRecordingFile() {
        return recordingFile;
    }

    public void setRecordingFile(String recordingFile) {
        this.recordingFile = recordingFile;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSpeechType() {
        return speechType;
    }

    public String getSpeechImpairmentDetails() {
        return speechImpairmentDetails;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public Map<String, Object> getAudioProperties() {
        return audioProperties;
    }

    public void addAudioProperty(String key, Object value) {
        this.audioProperties.put(key, value);
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void addAdditionalInfo(String key, String value) {
        this.additionalInfo.put(key, value);
    }
}