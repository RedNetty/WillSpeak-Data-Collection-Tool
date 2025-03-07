package com.rednetty.voicerecorder.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rednetty.voicerecorder.model.Phrase;
import com.rednetty.voicerecorder.model.RecordingMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles data loading and saving operations.
 * Updated to store each recording in its own directory with individual metadata.
 */
public class DataManager {
    private final String baseOutputDirectory;
    private final String globalMetadataFile;
    private final Gson gson;

    private List<Phrase> phrases;
    private List<RecordingMetadata> recordingsMetadata;

    public DataManager(String baseOutputDirectory) {
        this.baseOutputDirectory = baseOutputDirectory;
        this.globalMetadataFile = "recordings_index.json";
        // Use pretty printing for better readability
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.phrases = new ArrayList<>();
        this.recordingsMetadata = new ArrayList<>();

        // Create base output directory if it doesn't exist
        createBaseOutputDirectory();
    }

    public void createBaseOutputDirectory() {
        File dir = new File(baseOutputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<Phrase> loadPhrases(String phrasesJsonFile) {
        try {
            String jsonContent = readJsonFile(phrasesJsonFile);
            Type phraseListType = new TypeToken<ArrayList<Phrase>>(){}.getType();
            phrases = gson.fromJson(jsonContent, phraseListType);

            if (phrases == null || phrases.isEmpty()) {
                phrases = new ArrayList<>();
                // Add a fallback phrase if none are loaded
                phrases.add(new Phrase("fallback", "No phrases loaded from JSON. Please check your phrases.json file.", "general"));
            }
        } catch (IOException e) {
            System.err.println("Error loading phrases from JSON: " + e.getMessage());
            phrases = new ArrayList<>();
            // Add a fallback phrase if file cannot be loaded
            phrases.add(new Phrase("fallback", "Could not load phrases.json. Please create this file with your phrases.", "general"));
        }

        return phrases;
    }

    public List<RecordingMetadata> loadGlobalMetadata() {
        Path metadataPath = Paths.get(baseOutputDirectory, globalMetadataFile);
        if (Files.exists(metadataPath)) {
            try {
                String jsonContent = new String(Files.readAllBytes(metadataPath));
                Type metadataListType = new TypeToken<ArrayList<RecordingMetadata>>(){}.getType();
                recordingsMetadata = gson.fromJson(jsonContent, metadataListType);

                if (recordingsMetadata == null) {
                    recordingsMetadata = new ArrayList<>();
                }
            } catch (IOException e) {
                System.err.println("Error loading global metadata: " + e.getMessage());
                recordingsMetadata = new ArrayList<>();
            }
        }

        return recordingsMetadata;
    }

    public void saveGlobalMetadata(List<RecordingMetadata> metadata) {
        Path metadataPath = Paths.get(baseOutputDirectory, globalMetadataFile);
        try {
            String jsonContent = gson.toJson(metadata);
            Files.write(metadataPath, jsonContent.getBytes());
        } catch (IOException e) {
            System.err.println("Error saving global metadata: " + e.getMessage());
        }
    }

    /**
     * Adds a new recording metadata and saves both individual metadata file and updates global index.
     *
     * @param metadata The recording metadata to add
     * @param audioFilePath The full path to the audio file
     * @return true if successful, false otherwise
     */
    public boolean addRecordingWithMetadata(RecordingMetadata metadata, String audioFilePath) {
        if (recordingsMetadata == null) {
            recordingsMetadata = new ArrayList<>();
        }

        try {
            // Create a unique directory for this recording
            String recordingDirName = metadata.getRecordingId();
            File recordingDir = new File(baseOutputDirectory, recordingDirName);
            recordingDir.mkdirs();

            // Copy the audio file to the recording directory
            File sourceAudioFile = new File(audioFilePath);
            File destAudioFile = new File(recordingDir, sourceAudioFile.getName());

            // If the source is already in the final location, no need to copy
            if (!sourceAudioFile.getAbsolutePath().equals(destAudioFile.getAbsolutePath())) {
                Files.copy(sourceAudioFile.toPath(), destAudioFile.toPath());

                // Delete the original file if it was copied successfully
                if (destAudioFile.exists() && destAudioFile.length() == sourceAudioFile.length()) {
                    sourceAudioFile.delete();
                }
            }

            // Update the recording file path in metadata
            metadata.setRecordingFile(destAudioFile.getName());

            // Save individual metadata file
            File metadataFile = new File(recordingDir, "metadata.json");
            try (FileWriter writer = new FileWriter(metadataFile)) {
                writer.write(gson.toJson(metadata));
            }

            // Add to global metadata list and save
            recordingsMetadata.add(metadata);
            saveGlobalMetadata(recordingsMetadata);

            return true;
        } catch (IOException e) {
            System.err.println("Error saving recording metadata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a directory structure and returns the path for a new recording
     *
     * @param recordingId The unique ID for the recording
     * @return The path for the new recording file
     */
    public String createRecordingPath(String recordingId) {
        // Create a temporary directory path for the initial recording
        // (it will be moved to its final location after metadata is created)
        File tempDir = new File(baseOutputDirectory, "temp");
        tempDir.mkdirs();

        return tempDir.getPath() + File.separator + recordingId + ".wav";
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public List<RecordingMetadata> getRecordingsMetadata() {
        return recordingsMetadata;
    }

    private String readJsonFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
}