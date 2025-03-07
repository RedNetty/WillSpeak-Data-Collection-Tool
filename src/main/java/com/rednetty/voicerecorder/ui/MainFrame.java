package com.rednetty.voicerecorder.ui;

import com.rednetty.voicerecorder.model.Phrase;
import com.rednetty.voicerecorder.model.RecordingMetadata;
import com.rednetty.voicerecorder.model.UserProfile;
import com.rednetty.voicerecorder.util.DataManager;
import com.rednetty.voicerecorder.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Main application window frame that contains all UI components.
 * Updated to use the new file storage structure.
 */
public class MainFrame extends JFrame {
    private static final String TITLE = "Voice Recorder for AI Dataset Collection";
    private static final String OUTPUT_DIR = "recordings";
    private static final String PHRASES_FILE = "phrases.json";

    private UserProfile userProfile;
    private DataManager dataManager;
    private RecordingPanel recordingPanel;
    private JLabel userInfoLabel;

    public MainFrame() {
        super(TITLE);

        // Initialize data manager
        dataManager = new DataManager(OUTPUT_DIR);

        // Set up the user profile first
        if (!setupUserProfile()) {
            System.exit(0); // Exit if the user cancels the profile setup
        }

        // Set up UI
        setupUI();

        // Load phrases and update recording panel
        List<Phrase> phrases = dataManager.loadPhrases(PHRASES_FILE);

        // Load existing recordings metadata
        dataManager.loadGlobalMetadata();

        // Update recording panel with loaded phrases
        recordingPanel.setPhrases(phrases);

        // Set up window properties
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);

        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Show confirmation dialog
                int option = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // Save any remaining data
                    dataManager.saveGlobalMetadata(dataManager.getRecordingsMetadata());
                    System.exit(0);
                }
            }
        });
    }

    private boolean setupUserProfile() {
        // Create new profile or load existing one
        userProfile = new UserProfile();

        // Show profile setup dialog
        ProfileSetupDialog setupDialog = new ProfileSetupDialog(this, userProfile);
        return setupDialog.showDialog();
    }

    private void setupUI() {
        // Set system look and feel
        UIHelper.setupLookAndFeel();

        // Create main layout
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIHelper.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setOpaque(false);

        userInfoLabel = new JLabel(getUserDisplayName());
        userInfoLabel.setForeground(Color.WHITE);
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton profileButton = new JButton("Profile");
        profileButton.addActionListener(e -> showProfileDialog());

        userInfoPanel.add(userInfoLabel);
        userInfoPanel.add(profileButton);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);

        // Create recording panel with updated constructor
        recordingPanel = new RecordingPanel(
                dataManager.getPhrases(),
                userProfile,
                OUTPUT_DIR,
                dataManager,
                this::handleNewRecording
        );

        // Footer panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(240, 240, 240));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel infoLabel = new JLabel("Recording at 16kHz, 16-bit mono");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(e -> showHelpDialog());

        footerPanel.add(infoLabel, BorderLayout.WEST);
        footerPanel.add(helpButton, BorderLayout.EAST);

        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(recordingPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Handles a new recording by storing it with its metadata
     *
     * @param metadata the recording metadata
     * @param filePath the path to the audio file
     */
    private void handleNewRecording(RecordingMetadata metadata, String filePath) {
        // Add to data manager with individual metadata file
        dataManager.addRecordingWithMetadata(metadata, filePath);
    }

    private void showProfileDialog() {
        JDialog profileDialog = new JDialog(this, "User Profile", true);
        ProfilePanel profilePanel = new ProfilePanel(userProfile, this::updateUserProfile);

        profileDialog.setContentPane(profilePanel);
        profileDialog.pack();
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setResizable(false);
        profileDialog.setVisible(true);
    }

    private void updateUserProfile(UserProfile updatedProfile) {
        this.userProfile = updatedProfile;
        userInfoLabel.setText(getUserDisplayName());
        recordingPanel.setUserProfile(userProfile);
    }

    private String getUserDisplayName() {
        return userProfile.getName() != null && !userProfile.getName().isEmpty() ?
                userProfile.getName() : "Anonymous User";
    }

    private void showHelpDialog() {
        JDialog helpDialog = new JDialog(this, "Help", true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIHelper.createTitleLabel("Voice Recorder Help");

        JTextArea helpText = new JTextArea(
                "Voice Recorder for AI Dataset Collection\n\n" +
                        "This application allows you to record your voice for AI training purposes.\n\n" +
                        "Instructions:\n" +
                        "1. Navigate through phrases using the Previous and Next buttons\n" +
                        "2. Click Record to start recording yourself saying the displayed phrase\n" +
                        "3. Click Stop when finished\n" +
                        "4. Your recording will be saved automatically\n\n" +
                        "All recordings are saved as 16kHz WAV files in individual folders within\n" +
                        "the 'recordings' directory. Each recording has its own metadata.json file.\n\n" +
                        "Your user profile information is included with each recording to help\n" +
                        "researchers better understand speech patterns from different demographics.\n"
        );
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setBackground(panel.getBackground());

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> helpDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(helpText), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        helpDialog.setContentPane(panel);
        helpDialog.setSize(500, 400);
        helpDialog.setLocationRelativeTo(this);
        helpDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.setVisible(true);
        });
    }
}