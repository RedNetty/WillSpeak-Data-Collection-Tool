package com.rednetty.voicerecorder;

import com.rednetty.voicerecorder.ui.MainFrame;
import com.rednetty.voicerecorder.util.UIHelper;

import javax.swing.SwingUtilities;

/**
 * Main application class for the Phrase Recorder for AI Data Collection.
 */
public class PhraseRecorderApp {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Set up the system look and feel
        UIHelper.setupLookAndFeel();

        // Start the application on the EDT
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame app = new MainFrame();
                app.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}