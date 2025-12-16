package com.rednetty.voicerecorder.ui;

import com.rednetty.voicerecorder.model.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

/**
 * Panel for displaying and editing user profile information.
 */
public class ProfilePanel extends JPanel {
    private UserProfile userProfile;
    private Consumer<UserProfile> profileUpdateCallback;

    public ProfilePanel(UserProfile userProfile, Consumer<UserProfile> profileUpdateCallback) {
        this.userProfile = userProfile;
        this.profileUpdateCallback = profileUpdateCallback;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createProfileViewPanel(), BorderLayout.CENTER);
    }

    private JPanel createProfileViewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("User Profile Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // User ID
        gbc.gridy++;
        panel.add(new JLabel("User ID: " + userProfile.getUserId()), gbc);

        // Name
        gbc.gridy++;
        panel.add(new JLabel("Name: " + (userProfile.getName() != null ? userProfile.getName() : "Not provided")), gbc);

        // Age Group
        gbc.gridy++;
        panel.add(new JLabel("Age Group: " + (userProfile.getAgeGroup() != null ? userProfile.getAgeGroup() : "Not provided")), gbc);

        // Gender
        gbc.gridy++;
        panel.add(new JLabel("Gender: " + (userProfile.getGender() != null ? userProfile.getGender() : "Not provided")), gbc);

        // Native Language
        gbc.gridy++;
        panel.add(new JLabel("Native Language: " + (userProfile.getNativeLanguage() != null ? userProfile.getNativeLanguage() : "Not provided")), gbc);

        // Speech Type
        gbc.gridy++;
        panel.add(new JLabel("Speech Type: " + userProfile.getSpeechType()), gbc);

        // Speech Impairment Details (if applicable)
        if (userProfile.getSpeechType().equals("impaired") && userProfile.getSpeechImpairmentDetails() != null) {
            gbc.gridy++;
            panel.add(new JLabel("Speech Impairment Details: " + userProfile.getSpeechImpairmentDetails()), gbc);
        }

        // Device Info
        gbc.gridy++;
        panel.add(new JLabel("Device: " + userProfile.getDeviceInfo()), gbc);

        // Profile Created
        gbc.gridy++;
        panel.add(new JLabel("Profile Created: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userProfile.getProfileCreated())), gbc);

        // Edit button
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> showProfileEditDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void showProfileEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User Profile", true);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Edit User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Name field
        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name (optional):"), gbc);

        JTextField nameField = new JTextField(20);
        nameField.setText(userProfile.getName());
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Age group dropdown
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Age Group:"), gbc);

        String[] ageGroups = {"Under 18", "18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        JComboBox<String> ageGroupDropdown = new JComboBox<>(ageGroups);
        if (userProfile.getAgeGroup() != null) {
            ageGroupDropdown.setSelectedItem(userProfile.getAgeGroup());
        }
        gbc.gridx = 1;
        panel.add(ageGroupDropdown, gbc);

        // Gender dropdown
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);

        String[] genders = {"Male", "Female", "Non-binary", "Prefer not to say"};
        JComboBox<String> genderDropdown = new JComboBox<>(genders);
        if (userProfile.getGender() != null) {
            genderDropdown.setSelectedItem(userProfile.getGender());
        }
        gbc.gridx = 1;
        panel.add(genderDropdown, gbc);

        // Native language field
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Native Language:"), gbc);

        JTextField languageField = new JTextField(20);
        languageField.setText(userProfile.getNativeLanguage());
        gbc.gridx = 1;
        panel.add(languageField, gbc);

        // Speech type radio buttons
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Speech Type:"), gbc);

        JPanel speechTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup speechTypeGroup = new ButtonGroup();

        JRadioButton clearSpeechButton = new JRadioButton("Clear Speech");
        JRadioButton impairmentButton = new JRadioButton("Speech Impairment");

        speechTypeGroup.add(clearSpeechButton);
        speechTypeGroup.add(impairmentButton);
        speechTypePanel.add(clearSpeechButton);
        speechTypePanel.add(impairmentButton);

        // Set selected based on current profile
        if ("clear".equals(userProfile.getSpeechType())) {
            clearSpeechButton.setSelected(true);
        } else {
            impairmentButton.setSelected(true);
        }

        gbc.gridx = 1;
        panel.add(speechTypePanel, gbc);

        // Speech impairment details panel
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        JPanel impairmentPanel = new JPanel(new GridBagLayout());
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
        JComboBox<String> impairmentDropdown = new JComboBox<>(impairmentTypes);
        impGbc.gridx = 1;
        impairmentPanel.add(impairmentDropdown, impGbc);

        impGbc.gridx = 0;
        impGbc.gridy = 1;
        impairmentPanel.add(new JLabel("Additional Details:"), impGbc);

        JTextField impairmentDetailsField = new JTextField(20);
        impGbc.gridx = 1;
        impairmentPanel.add(impairmentDetailsField, impGbc);

        // Parse and set impairment details if available
        if (userProfile.getSpeechImpairmentDetails() != null) {
            String details = userProfile.getSpeechImpairmentDetails();
            int colonIndex = details.indexOf(':');
            if (colonIndex > 0) {
                String type = details.substring(0, colonIndex).trim();
                String additionalDetails = details.substring(colonIndex + 1).trim();
                for (int i = 0; i < impairmentTypes.length; i++) {
                    if (impairmentTypes[i].equals(type)) {
                        impairmentDropdown.setSelectedIndex(i);
                        break;
                    }
                }
                impairmentDetailsField.setText(additionalDetails);
            } else {
                // Try to find the type in dropdown
                for (int i = 0; i < impairmentTypes.length; i++) {
                    if (impairmentTypes[i].equals(details)) {
                        impairmentDropdown.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        impairmentPanel.setVisible("impaired".equals(userProfile.getSpeechType()));
        panel.add(impairmentPanel, gbc);

        // Show/hide impairment details based on selection
        clearSpeechButton.addActionListener(e -> impairmentPanel.setVisible(false));
        impairmentButton.addActionListener(e -> impairmentPanel.setVisible(true));

        // Add buttons
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, gbc);

        // Handle button actions
        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            // Validate required fields
            if (languageField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter your native language.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user profile
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

            // Notify about profile update
            if (profileUpdateCallback != null) {
                profileUpdateCallback.accept(userProfile);
            }

            // Refresh the display and close dialog
            removeAll();
            add(createProfileViewPanel(), BorderLayout.CENTER);
            revalidate();
            repaint();
            dialog.dispose();
        });

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}