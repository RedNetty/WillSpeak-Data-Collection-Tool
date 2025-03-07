package com.rednetty.voicerecorder.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Handles audio recording functionality.
 */
public class AudioRecorder {
    // Audio format constants
    private static final float SAMPLE_RATE = 16000.0f; // 16kHz
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1; // Mono
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;

    private final AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private boolean isRecording;
    private ByteArrayOutputStream audioData;
    private AudioLevelListener levelListener;

    /**
     * Interface for receiving audio level updates.
     */
    public interface AudioLevelListener {
        void onLevelUpdate(int level);
    }

    public AudioRecorder() {
        this.audioFormat = new AudioFormat(
                SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
        this.isRecording = false;
    }

    public void setAudioLevelListener(AudioLevelListener listener) {
        this.levelListener = listener;
    }

    public boolean startRecording() throws LineUnavailableException {
        // Configure the data line
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        if (!AudioSystem.isLineSupported(dataLineInfo)) {
            throw new LineUnavailableException("Audio line not supported");
        }

        targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        isRecording = true;
        audioData = new ByteArrayOutputStream();

        // Start the recording thread
        Thread captureThread = new Thread(this::captureAudio);
        captureThread.start();

        return true;
    }

    public void stopRecording() {
        if (isRecording && targetDataLine != null) {
            isRecording = false;
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean saveRecording(String filePath) throws IOException {
        if (audioData == null || audioData.size() == 0) {
            return false;
        }

        byte[] audioBytes = audioData.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        AudioInputStream recordedAIS = new AudioInputStream(
                bais, audioFormat, audioBytes.length / audioFormat.getFrameSize());

        // Ensure directory exists
        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs();

        // Write to WAV file
        AudioSystem.write(recordedAIS, AudioFileFormat.Type.WAVE, outputFile);

        return true;
    }

    private void captureAudio() {
        byte[] buffer = new byte[4096];
        int bytesRead;

        while (isRecording) {
            bytesRead = targetDataLine.read(buffer, 0, buffer.length);

            if (bytesRead > 0) {
                audioData.write(buffer, 0, bytesRead);

                // Calculate audio level for visualization
                if (levelListener != null) {
                    int level = calculateAudioLevel(buffer, bytesRead);
                    levelListener.onLevelUpdate(level);
                }
            }
        }
    }

    private int calculateAudioLevel(byte[] buffer, int bytesRead) {
        int sum = 0;
        // Process 16-bit samples
        for (int i = 0; i < bytesRead; i += 2) {
            if (i + 1 < bytesRead) {
                int sample = (buffer[i] & 0xFF) | ((buffer[i + 1] & 0xFF) << 8);
                if ((sample & 0x8000) != 0) {
                    sample = -((sample ^ 0xFFFF) + 1); // Convert to signed
                }
                sum += Math.abs(sample);
            }
        }

        int average = bytesRead > 0 ? sum / (bytesRead / 2) : 0;
        // Scale 0-32767 to 0-100
        return (int)(average / 327.67);
    }

    // Provides access to audio format constants for metadata
    public static float getSampleRate() {
        return SAMPLE_RATE;
    }

    public static int getSampleSizeInBits() {
        return SAMPLE_SIZE_IN_BITS;
    }

    public static int getChannels() {
        return CHANNELS;
    }

    public static boolean isSigned() {
        return SIGNED;
    }

    public static boolean isBigEndian() {
        return BIG_ENDIAN;
    }
}