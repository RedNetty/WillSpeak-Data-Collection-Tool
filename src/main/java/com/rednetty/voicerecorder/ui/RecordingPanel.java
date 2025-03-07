package com.rednetty.voicerecorder.ui;

import com.rednetty.voicerecorder.audio.AudioRecorder;
import com.rednetty.voicerecorder.model.Phrase;
import com.rednetty.voicerecorder.model.RecordingMetadata;
import com.rednetty.voicerecorder.model.UserProfile;
import com.rednetty.voicerecorder.util.DataManager;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Panel for displaying phrases and recording user's voice.
 * Updated to use the new file storage structure.
 */
public class RecordingPanel extends JPanel {
    // UI components
    private JLabel phraseLabel;
    private JButton recordButton;
    private JButton nextButton;
    private JButton previousButton;
    private JProgressBar levelMeter;
    private JLabel statusLabel;
    private JLabel phraseCountLabel;

    // Colors
    private final Color PRIMARY_COLOR = new Color(66, 133, 244);
    private final Color ACCENT_COLOR = new Color(219, 68, 55);
    private final Color SUCCESS_COLOR = new Color(15, 157, 88);

    // Data
    private List<Phrase> phrases;
    private int currentPhraseIndex = 0;
    private UserProfile userProfile;
    private String outputDirectory;
    private DataManager dataManager;

    // Audio recording
    private AudioRecorder audioRecorder;
    private BiConsumer<RecordingMetadata, String> recordingCallback;

    public RecordingPanel(List<Phrase> phrases, UserProfile userProfile, String outputDirectory,
                          DataManager dataManager, BiConsumer<RecordingMetadata, String> recordingCallback) {
        this.phrases = phrases;
        this.userProfile = userProfile;
        this.outputDirectory = outputDirectory;
        this.dataManager = dataManager;
        this.recordingCallback = recordingCallback;
        this.audioRecorder = new AudioRecorder();

        setupUI();
        updatePhraseDisplay();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Phrase display panel
        JPanel phrasePanel = new JPanel(new BorderLayout());
        phrasePanel.setBorder(new CompoundBorder(
                new EmptyBorder(20, 20, 10, 20),
                new CompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(20, 20, 20, 20)
                )
        ));
        phrasePanel.setBackground(Color.WHITE);

        phraseLabel = new JLabel("Loading phrases...");
        phraseLabel.setFont(new Font("Arial", Font.BOLD, 24));
        phraseLabel.setHorizontalAlignment(JLabel.CENTER);
        phraseLabel.setForeground(new Color(33, 33, 33));

        phraseCountLabel = new JLabel("0 of 0");
        phraseCountLabel.setHorizontalAlignment(JLabel.CENTER);
        phraseCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        phraseCountLabel.setForeground(new Color(100, 100, 100));

        JPanel phraseLabelPanel = new JPanel(new BorderLayout());
        phraseLabelPanel.setOpaque(false);
        phraseLabelPanel.add(phraseLabel, BorderLayout.CENTER);
        phraseLabelPanel.add(phraseCountLabel, BorderLayout.SOUTH);

        phrasePanel.add(phraseLabelPanel, BorderLayout.CENTER);

        // Recording control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        controlPanel.setOpaque(false);

        // Level meter panel
        JPanel meterPanel = new JPanel(new BorderLayout());
        meterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        meterPanel.setOpaque(false);

        levelMeter = new JProgressBar(0, 100);
        levelMeter.setStringPainted(true);
        levelMeter.setString("Audio Level");
        levelMeter.setFont(new Font("Arial", Font.PLAIN, 12));
        levelMeter.setForeground(PRIMARY_COLOR);
        meterPanel.add(levelMeter, BorderLayout.CENTER);

        // Status label
        statusLabel = new JLabel("Ready to record. Press the Record button.");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        meterPanel.add(statusLabel, BorderLayout.SOUTH);

        controlPanel.add(meterPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        previousButton = createStyledButton("Previous", new Color(100, 100, 100));
        previousButton.addActionListener(e -> {
            if (currentPhraseIndex > 0) {
                currentPhraseIndex--;
                updatePhraseDisplay();
            }
        });

        recordButton = createStyledButton("Record", ACCENT_COLOR);
        recordButton.setFont(new Font("Arial", Font.BOLD, 16));
        recordButton.setPreferredSize(new Dimension(120, 50));
        recordButton.setForeground(Color.WHITE);
        recordButton.addActionListener(e -> {
            if (!audioRecorder.isRecording()) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        nextButton = createStyledButton("Next", new Color(100, 100, 100));
        nextButton.addActionListener(e -> {
            if (currentPhraseIndex < phrases.size() - 1) {
                currentPhraseIndex++;
                updatePhraseDisplay();
            }
        });

        buttonPanel.add(previousButton);
        buttonPanel.add(recordButton);
        buttonPanel.add(nextButton);

        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add panels to main layout
        add(phrasePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Set up audio level listener
        audioRecorder.setAudioLevelListener(level -> {
            SwingUtilities.invokeLater(() -> levelMeter.setValue(level));
        });
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void updatePhraseDisplay() {
        if (!phrases.isEmpty() && currentPhraseIndex >= 0 && currentPhraseIndex < phrases.size()) {
            Phrase currentPhrase = phrases.get(currentPhraseIndex);
            phraseLabel.setText("<html><div style='text-align: center;'>" +
                    currentPhrase.getText() + "</div></html>");

            phraseCountLabel.setText((currentPhraseIndex + 1) + " of " + phrases.size());

            previousButton.setEnabled(currentPhraseIndex > 0);
            nextButton.setEnabled(currentPhraseIndex < phrases.size() - 1);
        } else {
            phraseLabel.setText("No phrases available");
            phraseCountLabel.setText("0 of 0");
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
    }

    private void startRecording() {
        try {
            statusLabel.setText("Recording... Speak now!");
            recordButton.setText("Stop");
            recordButton.setBackground(SUCCESS_COLOR);

            audioRecorder.startRecording();
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not start recording: " + e.getMessage(),
                    "Recording Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopRecording() {
        audioRecorder.stopRecording();
        recordButton.setText("Record");
        recordButton.setBackground(ACCENT_COLOR);
        statusLabel.setText("Saving recording...");

        // Save the recording in a separate thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Get the current phrase
                Phrase currentPhrase = phrases.get(currentPhraseIndex);

                // Generate a unique recording ID
                String recordingId = UUID.randomUUID().toString();

                // Create file path in temporary location
                String filePath = dataManager.createRecordingPath(recordingId);

                // Generate timestamp
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                // Save the recording
                if (audioRecorder.saveRecording(filePath)) {
                    // Create metadata
                    RecordingMetadata metadata = new RecordingMetadata(
                            userProfile.getUserId(),
                            currentPhrase.getId(),
                            currentPhrase.getText(),
                            currentPhrase.getCategory(),
                            recordingId + ".wav", // Temporary filename, will be updated by DataManager
                            timestamp,
                            userProfile.getSpeechType(),
                            userProfile.getSpeechImpairmentDetails(),
                            userProfile.getDeviceInfo()
                    );

                    // Set the recording ID
                    metadata.setRecordingId(recordingId);

                    // Add audio properties to metadata
                    metadata.addAudioProperty("sampleRate", AudioRecorder.getSampleRate());
                    metadata.addAudioProperty("bitsPerSample", AudioRecorder.getSampleSizeInBits());
                    metadata.addAudioProperty("channels", AudioRecorder.getChannels());

                    // Notify callback with metadata and file path
                    if (recordingCallback != null) {
                        recordingCallback.accept(metadata, filePath);
                    }

                    statusLabel.setText("Recording saved. Ready for next phrase.");

                    // Automatically go to next phrase if not at the end
                    if (currentPhraseIndex < phrases.size() - 1) {
                        currentPhraseIndex++;
                        updatePhraseDisplay();
                    }
                } else {
                    statusLabel.setText("Error saving recording. Try again.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving recording: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error saving recording. Try again.");
            }
        });
    }

    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
        currentPhraseIndex = 0;
        updatePhraseDisplay();
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}