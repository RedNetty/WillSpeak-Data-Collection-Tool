package com.rednetty.voicerecorder.ui;

import com.rednetty.voicerecorder.model.UserProfile;
import com.rednetty.voicerecorder.util.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for setting up the user profile before recording.
 */
public class ProfileSetupDialog extends JDialog {
    private UserProfile userProfile;
    private boolean confirmed = false;

    // UI Components
    private JTextField nameField;
    private JComboBox<String> ageGroupDropdown;
    private JComboBox<String> genderDropdown;
    private JTextField languageField;
    private JRadioButton clearSpeechButton;
    private JRadioButton impairmentButton;
    private JPanel impairmentPanel;
    private JComboBox<String> impairmentDropdown;
    private JTextField impairmentDetailsField;

    public ProfileSetupDialog(Frame owner, UserProfile userProfile) {
        super(owner, "User Profile Setup", true);
        this.userProfile = userProfile;

        setupUI();

        // Configure dialog
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = UIHelper.createTitleLabel("User Profile Setup");
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Name field
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name (optional):"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Age group dropdown
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Age Group:"), gbc);

        String[] ageGroups = {"Under 18", "18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        ageGroupDropdown = new JComboBox<>(ageGroups);
        gbc.gridx = 1;
        panel.add(ageGroupDropdown, gbc);

        // Gender dropdown
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);

        String[] genders = {"Male", "Female", "Non-binary", "Prefer not to say"};
        genderDropdown = new JComboBox<>(genders);
        gbc.gridx = 1;
        panel.add(genderDropdown, gbc);

        // Native language field
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Native Language:"), gbc);

        languageField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(languageField, gbc);

        // Speech type radio buttons
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Speech Type:"), gbc);

        JPanel speechTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup speechTypeGroup = new ButtonGroup();

        clearSpeechButton = new JRadioButton("Clear Speech");
        clearSpeechButton.setSelected(true);
        impairmentButton = new JRadioButton("Speech Impairment");

        speechTypeGroup.add(clearSpeechButton);
        speechTypeGroup.add(impairmentButton);
        speechTypePanel.add(clearSpeechButton);
        speechTypePanel.add(impairmentButton);

        gbc.gridx = 1;
        panel.add(speechTypePanel, gbc);

        // Speech impairment details panel (initially hidden)
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        impairmentPanel = new JPanel(new GridBagLayout());
        impairmentPanel.setBorder(BorderFactory.createTitledBorder("Speech Impairment Details"));

        GridBagConstraints impGbc = new GridBagConstraints();
        impGbc.gridx = 0;
        impGbc.gridy = 0;
        impGbc.anchor = GridBagConstraints.WEST;
        impGbc.insets = new Insets(2, 2, 2, 2);

        impairmentPanel.add(new JLabel("Type of Impairment:"), impGbc);

        String[] impairmentTypes = {
                "Stuttering", "Apraxia", "Dysarthria", "Cluttering",
                "Lisping", "Articulation Disorder", "Vocal Cord Dysfunction", "Other"
        };
        impairmentDropdown = new JComboBox<>(impairmentTypes);
        impGbc.gridx = 1;
        impairmentPanel.add(impairmentDropdown, impGbc);

        impGbc.gridx = 0;
        impGbc.gridy = 1;
        impairmentPanel.add(new JLabel("Additional Details:"), impGbc);

        impairmentDetailsField = new JTextField(20);
        impGbc.gridx = 1;
        impairmentPanel.add(impairmentDetailsField, impGbc);

        impairmentPanel.setVisible(false);
        panel.add(impairmentPanel, gbc);

        // Show/hide impairment details based on selection
        clearSpeechButton.addActionListener(e -> impairmentPanel.setVisible(false));
        impairmentButton.addActionListener(e -> impairmentPanel.setVisible(true));

        // Add validation and OK/Cancel buttons
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, gbc);

        // Handle button clicks
        okButton.addActionListener(e -> {
            // Validate required fields
            if (languageField.getText().trim().isEmpty()) {
                UIHelper.showErrorDialog(this,
                        "Please enter your native language.",
                        "Validation Error");
                return;
            }

            // Set user profile data
            userProfile.setName(nameField.getText().trim());
            userProfile.setAgeGroup((String) ageGroupDropdown.getSelectedItem());
            userProfile.setGender((String) genderDropdown.getSelectedItem());
            userProfile.setNativeLanguage(languageField.getText().trim());

            if (clearSpeechButton.isSelected()) {
                userProfile.setSpeechType("clear");
                userProfile.setSpeechImpairmentDetails(null);
            } else {
                userProfile.setSpeechType("impaired");
                String impairmentType = (String) impairmentDropdown.getSelectedItem();
                String details = impairmentDetailsField.getText().trim();
                userProfile.setSpeechImpairmentDetails(
                        impairmentType + (details.isEmpty() ? "" : ": " + details)
                );
            }

            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        setContentPane(panel);
    }

    /**
     * Shows the dialog and returns whether the user confirmed the profile.
     *
     * @return true if the profile was confirmed, false if canceled
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }
}