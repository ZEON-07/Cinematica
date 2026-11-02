package com.movierating.ui;

import com.movierating.database.DatabaseOperations;
import com.movierating.exceptions.EmptyFieldException;
import com.movierating.models.BaseUser;
import com.movierating.models.Movie;
import com.movierating.models.Review;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MovieDetailsFrame extends JDialog {

    private final MainDashboardFrame parentDashboard;
    private final Movie currentMovie;
    private final BaseUser currentUser;
    private final DatabaseOperations dbManager;

    private JPanel contentPanel;
    private JPanel detailsPanel;
    private JPanel reviewsPanel;
    private JPanel newReviewPanel;

    private JLabel titleLabel;
    private JLabel genreLabel;
    private JLabel rewatchLabel;
    private JLabel ratingLabel;
    private JTextArea descArea;

    private DefaultListModel<String> reviewListModel;
    private JList<String> reviewList;
    private JScrollPane reviewsScrollPane;

    private JComboBox<Integer> ratingComboBox;
    private JTextArea reviewInputField;
    private JButton submitBtn;

    public MovieDetailsFrame(MainDashboardFrame parent, Movie movie, BaseUser user, DatabaseOperations dbManager) {
        super(parent, "Movie Details - " + movie.getTitle(), true);
        this.parentDashboard = parent;
        this.currentMovie = movie;
        this.currentUser = user;
        this.dbManager = dbManager;

        initUI();
        loadReviews();
    }

    private void initUI() {
        setSize(760, 680);
        setLocationRelativeTo(parentDashboard);
        setLayout(new BorderLayout(10, 10));

        contentPanel = new JPanel(new BorderLayout(10, 12));
        contentPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        contentPanel.setBackground(UIHelper.getBgColor());

        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(UIHelper.getCardBgColor());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor(), 1),
                new EmptyBorder(14, 18, 14, 18)
        ));

        titleLabel = new JLabel(currentMovie.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UIHelper.getTextColor());

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        metaRow.setOpaque(false);

        genreLabel = new JLabel("Genre: " + currentMovie.getGenre());
        genreLabel.setFont(UIHelper.FONT_BOLD);
        genreLabel.setForeground(UIHelper.COLOR_PRIMARY);

        rewatchLabel = new JLabel("Rewatchable: " + (currentMovie.isRewatchable() ? "Yes" : "No"));
        rewatchLabel.setFont(UIHelper.FONT_BOLD);
        rewatchLabel.setForeground(currentMovie.isRewatchable() ? UIHelper.COLOR_SUCCESS : UIHelper.getMutedTextColor());

        ratingLabel = new JLabel();
        ratingLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 15));
        ratingLabel.setForeground(new Color(230, 126, 34));
        updateRatingLabelText();

        metaRow.add(genreLabel);
        metaRow.add(rewatchLabel);
        metaRow.add(ratingLabel);

        String desc = currentMovie.getDescription();
        descArea = new JTextArea((desc == null || desc.trim().isEmpty()) ? "No additional description provided." : desc);
        descArea.setFont(UIHelper.FONT_REGULAR);
        descArea.setForeground(UIHelper.getTextColor());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setBorder(new EmptyBorder(10, 0, 0, 0));

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(metaRow);
        detailsPanel.add(Box.createVerticalStrut(6));
        detailsPanel.add(descArea);

        contentPanel.add(detailsPanel, BorderLayout.NORTH);

        reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                "Reviews & Ratings",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                UIHelper.FONT_BOLD,
                UIHelper.getTextColor()
        ));
        reviewsPanel.setBackground(UIHelper.getCardBgColor());

        reviewListModel = new DefaultListModel<>();
        reviewList = new JList<>(reviewListModel);
        reviewList.setFont(UIHelper.FONT_REGULAR);
        reviewList.setBackground(UIHelper.getCardBgColor());
        reviewList.setForeground(UIHelper.getTextColor());
        reviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        reviewList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 13));
                lbl.setBackground(UIHelper.getCardBgColor());
                lbl.setForeground(UIHelper.getTextColor());
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIHelper.getBorderColor()),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                return lbl;
            }
        });

        reviewsScrollPane = new JScrollPane(reviewList);
        reviewsScrollPane.setPreferredSize(new Dimension(680, 180));
        reviewsScrollPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        reviewsScrollPane.setBackground(UIHelper.getCardBgColor());
        reviewsScrollPane.getViewport().setBackground(UIHelper.getCardBgColor());
        reviewsPanel.add(reviewsScrollPane, BorderLayout.CENTER);

        contentPanel.add(reviewsPanel, BorderLayout.CENTER);

        newReviewPanel = new JPanel(new BorderLayout(8, 8));
        newReviewPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                "Add Rating & Review",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                UIHelper.FONT_BOLD,
                UIHelper.getTextColor()
        ));
        newReviewPanel.setBackground(UIHelper.getCardBgColor());

        JPanel headerReviewRow = new JPanel(new BorderLayout(10, 0));
        headerReviewRow.setOpaque(false);

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        ratingRow.setOpaque(false);

        JLabel lblSelectRating = new JLabel("Rating (1 to 5) *:");
        lblSelectRating.setFont(UIHelper.FONT_BOLD);
        lblSelectRating.setForeground(UIHelper.getTextColor());
        ratingRow.add(lblSelectRating);

        ratingComboBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        ratingComboBox.setFont(UIHelper.FONT_BOLD);
        ratingComboBox.setPreferredSize(new Dimension(75, 30));
        ratingComboBox.setBackground(UIHelper.getInputBgColor());
        ratingComboBox.setForeground(UIHelper.getInputTextColor());
        ratingRow.add(ratingComboBox);

        JPanel authorRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        authorRow.setOpaque(false);

        JLabel authorHint = new JLabel("User: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        authorHint.setFont(UIHelper.FONT_SMALL);
        authorHint.setForeground(UIHelper.getMutedTextColor());
        authorRow.add(authorHint);

        headerReviewRow.add(ratingRow, BorderLayout.WEST);
        headerReviewRow.add(authorRow, BorderLayout.EAST);
        newReviewPanel.add(headerReviewRow, BorderLayout.NORTH);

        reviewInputField = new JTextArea(3, 40);
        reviewInputField.setFont(UIHelper.FONT_REGULAR);
        reviewInputField.setLineWrap(true);
        reviewInputField.setWrapStyleWord(true);
        reviewInputField.setBackground(UIHelper.getInputBgColor());
        reviewInputField.setForeground(UIHelper.getInputTextColor());
        reviewInputField.setCaretColor(UIHelper.getInputTextColor());

        JScrollPane textScroll = new JScrollPane(reviewInputField);
        textScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor()),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        newReviewPanel.add(textScroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnRow.setOpaque(false);

        submitBtn = UIHelper.createCustomButton("Submit Review", UIHelper.COLOR_PRIMARY, Color.WHITE);
        submitBtn.addActionListener(e -> handleSubmitReview());

        JButton closeBtn = UIHelper.createSecondaryButton("Close Window");
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(submitBtn);
        btnRow.add(closeBtn);
        newReviewPanel.add(btnRow, BorderLayout.SOUTH);

        contentPanel.add(newReviewPanel, BorderLayout.SOUTH);
        add(contentPanel);
    }

    private void updateRatingLabelText() {
        if (currentMovie.getAverageRating() > 0.0) {
            ratingLabel.setText("<html>Rating: <span style='color:#e67e22;'>★</span> " + String.format("%.1f / 5.0", currentMovie.getAverageRating()) + "</html>");
        } else {
            ratingLabel.setText("Rating: No rating yet");
        }
    }

    private void loadReviews() {
        reviewListModel.clear();
        try {
            List<Review> reviews = dbManager.getReviewsForMovie(currentMovie.getMovieId());
            String starColor = "#e67e22";
            String userColor = "#2c3e50";
            String textColor = "#333333";
            String mutedColor = "#888888";

            if (reviews.isEmpty()) {
                reviewListModel.addElement("<html><body style='padding:8px; color:" + mutedColor + ";'>No reviews recorded yet.</body></html>");
            } else {
                for (Review rev : reviews) {
                    String starsHtml = "<span style='color:" + starColor + "; font-family: Segoe UI Symbol; font-weight:bold;'>"
                            + rev.getRatingStars() + "</span> <b>(" + rev.getRating() + "/5)</b>";

                    String text = rev.getReviewText();
                    String reviewBody = (text == null || text.trim().isEmpty())
                            ? "<i style='color:" + mutedColor + ";'>(Rating only - no written review)</i>"
                            : "<span style='color:" + textColor + ";'>" + text + "</span>";

                    String formattedReview = "<html><body style='width: 580px; padding: 4px;'>"
                            + starsHtml + " &nbsp;|&nbsp; <span style='color:" + userColor + "; font-weight:bold;'>"
                            + rev.getReviewerUsername() + "</span>: "
                            + reviewBody
                            + "</body></html>";

                    reviewListModel.addElement(formattedReview);
                }
            }

            Movie updatedMovie = dbManager.getMovieById(currentMovie.getMovieId());
            if (updatedMovie != null) {
                currentMovie.setAverageRating(updatedMovie.getAverageRating());
                updateRatingLabelText();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load reviews: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmitReview() {
        String comment = reviewInputField.getText();
        Integer rating = (Integer) ratingComboBox.getSelectedItem();

        try {
            if (rating == null) {
                throw new EmptyFieldException("Please select a valid rating score.");
            }

            Review newReview = new Review();
            newReview.setMovieId(currentMovie.getMovieId());
            newReview.setUserId(currentUser.getUserId());
            newReview.setRating(rating);
            newReview.setReviewText((comment != null) ? comment.trim() : "");

            dbManager.addReview(newReview);

            JOptionPane.showMessageDialog(this,
                    "Thank you! Your rating has been recorded.",
                    "Rating Submitted", JOptionPane.INFORMATION_MESSAGE);

            reviewInputField.setText("");
            loadReviews();
            parentDashboard.refreshMovieList();

        } catch (EmptyFieldException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Required", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
