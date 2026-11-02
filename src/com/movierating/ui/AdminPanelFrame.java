package com.movierating.ui;

import com.movierating.database.DatabaseOperations;
import com.movierating.exceptions.EmptyFieldException;
import com.movierating.models.BaseUser;
import com.movierating.models.Movie;
import com.movierating.models.Review;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class AdminPanelFrame extends JDialog {

    private final MainDashboardFrame parentDashboard;
    private final BaseUser currentUser;
    private final DatabaseOperations dbManager;

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;

    private JTextField titleField;
    private JComboBox<String> genreComboBox;
    private JComboBox<String> ratingComboBox;
    private JCheckBox rewatchableCheckBox;
    private JTextArea reviewArea;
    private JTextArea descArea;
    private JButton saveBtn;

    public AdminPanelFrame(MainDashboardFrame parent, BaseUser currentUser, DatabaseOperations dbManager) {
        super(parent, "Add Movie & Review", true);
        this.parentDashboard = parent;
        this.currentUser = currentUser;
        this.dbManager = dbManager;

        initUI();
    }

    private void initUI() {
        setSize(560, 620);
        setLocationRelativeTo(parentDashboard);
        setResizable(false);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        mainPanel.setBackground(UIHelper.getBgColor());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIHelper.getHeaderBgColor());
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLbl = new JLabel("Add Movie to Your Collection");
        titleLbl.setFont(UIHelper.FONT_HEADER);
        titleLbl.setForeground(Color.WHITE);
        headerPanel.add(titleLbl, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIHelper.getCardBgColor());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                new EmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTitle = new JLabel("Movie Title *:");
        lblTitle.setFont(UIHelper.FONT_BOLD);
        lblTitle.setForeground(UIHelper.getTextColor());
        formPanel.add(lblTitle, gbc);

        gbc.gridx = 1;
        titleField = new JTextField(18);
        titleField.setFont(UIHelper.FONT_REGULAR);
        titleField.setPreferredSize(new Dimension(240, 32));
        titleField.setBackground(UIHelper.getInputBgColor());
        titleField.setForeground(UIHelper.getInputTextColor());
        formPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblGenre = new JLabel("Genre *:");
        lblGenre.setFont(UIHelper.FONT_BOLD);
        lblGenre.setForeground(UIHelper.getTextColor());
        formPanel.add(lblGenre, gbc);

        gbc.gridx = 1;
        String[] defaultGenres = {
                "Action", "Adventure", "Animation", "Comedy", "Crime",
                "Documentary", "Drama", "Fantasy", "Horror", "Mystery",
                "Romance", "Sci-Fi", "Thriller"
        };
        genreComboBox = new JComboBox<>(defaultGenres);
        genreComboBox.setFont(UIHelper.FONT_REGULAR);
        genreComboBox.setPreferredSize(new Dimension(240, 32));
        genreComboBox.setBackground(UIHelper.getInputBgColor());
        genreComboBox.setForeground(UIHelper.getInputTextColor());
        formPanel.add(genreComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblRating = new JLabel("Your Rating *:");
        lblRating.setFont(UIHelper.FONT_BOLD);
        lblRating.setForeground(UIHelper.getTextColor());
        formPanel.add(lblRating, gbc);

        gbc.gridx = 1;
        String[] ratings = {
                "5 Stars - Excellent",
                "4 Stars - Very Good",
                "3 Stars - Good",
                "2 Stars - Fair",
                "1 Star - Poor"
        };
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setFont(UIHelper.FONT_REGULAR);
        ratingComboBox.setPreferredSize(new Dimension(240, 32));
        ratingComboBox.setBackground(UIHelper.getInputBgColor());
        ratingComboBox.setForeground(UIHelper.getInputTextColor());
        formPanel.add(ratingComboBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        rewatchableCheckBox = new JCheckBox("Would rewatch this movie");
        rewatchableCheckBox.setFont(UIHelper.FONT_BOLD);
        rewatchableCheckBox.setForeground(UIHelper.getTextColor());
        rewatchableCheckBox.setOpaque(false);
        formPanel.add(rewatchableCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblReview = new JLabel("Your Review (Optional):");
        lblReview.setFont(UIHelper.FONT_BOLD);
        lblReview.setForeground(UIHelper.getTextColor());
        formPanel.add(lblReview, gbc);

        gbc.gridx = 1;
        reviewArea = new JTextArea(3, 18);
        reviewArea.setFont(UIHelper.FONT_REGULAR);
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(UIHelper.getInputBgColor());
        reviewArea.setForeground(UIHelper.getInputTextColor());
        JScrollPane reviewScroll = new JScrollPane(reviewArea);
        reviewScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                new EmptyBorder(4, 4, 4, 4)
        ));
        formPanel.add(reviewScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblDesc = new JLabel("Description (Optional):");
        lblDesc.setFont(UIHelper.FONT_BOLD);
        lblDesc.setForeground(UIHelper.getTextColor());
        formPanel.add(lblDesc, gbc);

        gbc.gridx = 1;
        descArea = new JTextArea(2, 18);
        descArea.setFont(UIHelper.FONT_REGULAR);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(UIHelper.getInputBgColor());
        descArea.setForeground(UIHelper.getInputTextColor());
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                new EmptyBorder(4, 4, 4, 4)
        ));
        formPanel.add(descScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnPanel.setOpaque(false);

        saveBtn = UIHelper.createCustomButton("Save Movie", UIHelper.COLOR_SUCCESS, Color.WHITE);
        saveBtn.addActionListener(e -> handleSaveMovie());

        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        UIHelper.linkEnterKeyToButton(titleField, saveBtn);
        add(mainPanel);
    }

    private void handleSaveMovie() {
        String title = titleField.getText();
        Object selectedGenre = genreComboBox.getSelectedItem();
        String genre = (selectedGenre != null) ? selectedGenre.toString() : "";
        String reviewText = reviewArea.getText();
        String description = descArea.getText();
        boolean isRewatchable = rewatchableCheckBox.isSelected();

        int ratingValue = 5 - ratingComboBox.getSelectedIndex();

        try {
            if (title == null || title.trim().isEmpty()) {
                throw new EmptyFieldException("Movie title cannot be empty.");
            }
            if (genre == null || genre.trim().isEmpty()) {
                throw new EmptyFieldException("Movie genre cannot be empty.");
            }

            Movie newMovie = new Movie(
                    title.trim(),
                    genre.trim(),
                    (description != null) ? description.trim() : "",
                    currentUser.getUserId(),
                    ratingValue,
                    isRewatchable
            );

            Review newReview = new Review(
                    0,
                    currentUser.getUserId(),
                    ratingValue,
                    (reviewText != null) ? reviewText.trim() : ""
            );

            dbManager.addMovieWithReview(newMovie, newReview);

            JOptionPane.showMessageDialog(this,
                    "Movie '" + newMovie.getTitle() + "' successfully saved to your collection!",
                    "Movie Saved", JOptionPane.INFORMATION_MESSAGE);

            parentDashboard.refreshMovieList();
            dispose();

        } catch (EmptyFieldException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Required", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to save movie: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
