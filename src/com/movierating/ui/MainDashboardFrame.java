package com.movierating.ui;

import com.movierating.database.DatabaseOperations;
import com.movierating.models.BaseUser;
import com.movierating.models.Movie;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardFrame extends JFrame {

    private final BaseUser currentUser;
    private final DatabaseOperations dbManager;

    private List<Movie> currentDisplayedMovies = new ArrayList<>();

    private JPanel mainPanel;
    private JPanel topContainer;
    private JPanel bannerPanel;
    private JPanel toolbarPanel;
    private JPanel bottomContainer;

    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private JComboBox<String> genreFilterCombo;
    private JButton searchBtn;
    private JButton resetBtn;
    private JLabel statusLabel;
    private JLabel lblSearch;
    private JLabel lblGenre;

    public MainDashboardFrame(BaseUser currentUser, DatabaseOperations dbManager) {
        this.currentUser = currentUser;
        this.dbManager = dbManager;
        initUI();
        loadMoviesToTable(null, null);
    }

    private void initUI() {
        setTitle("Cinematica - My Collection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        mainPanel.setBackground(UIHelper.getBgColor());

        topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);

        bannerPanel = new JPanel(new BorderLayout(10, 0));
        bannerPanel.setBackground(UIHelper.getHeaderBgColor());
        bannerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel greetingLabel = new JLabel(currentUser.getDashboardGreeting());
        greetingLabel.setFont(UIHelper.FONT_HEADER);
        greetingLabel.setForeground(Color.WHITE);

        JPanel headerRightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRightGroup.setOpaque(false);

        JButton logoutBtn = UIHelper.createCustomButton("Sign Out", UIHelper.COLOR_DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> handleLogout());
        headerRightGroup.add(logoutBtn);

        bannerPanel.add(greetingLabel, BorderLayout.WEST);
        bannerPanel.add(headerRightGroup, BorderLayout.EAST);
        topContainer.add(bannerPanel, BorderLayout.NORTH);

        toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setBackground(UIHelper.getCardBgColor());
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIHelper.getBorderColor(), 1),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        searchControls.setOpaque(false);

        lblSearch = new JLabel("Search Title:");
        lblSearch.setFont(UIHelper.FONT_BOLD);
        lblSearch.setForeground(UIHelper.getTextColor());
        searchControls.add(lblSearch);

        searchField = new JTextField(15);
        searchField.setFont(UIHelper.FONT_REGULAR);
        searchField.setPreferredSize(new Dimension(160, 32));
        searchField.setBackground(UIHelper.getInputBgColor());
        searchField.setForeground(UIHelper.getInputTextColor());
        searchControls.add(searchField);

        lblGenre = new JLabel("Genre:");
        lblGenre.setFont(UIHelper.FONT_BOLD);
        lblGenre.setForeground(UIHelper.getTextColor());
        searchControls.add(lblGenre);

        genreFilterCombo = new JComboBox<>(new String[]{
                "All Genres", "Action", "Adventure", "Animation", "Comedy", "Crime",
                "Documentary", "Drama", "Fantasy", "Horror", "Mystery",
                "Romance", "Sci-Fi", "Thriller"
        });
        genreFilterCombo.setFont(UIHelper.FONT_REGULAR);
        genreFilterCombo.setPreferredSize(new Dimension(130, 32));
        genreFilterCombo.setBackground(UIHelper.getInputBgColor());
        genreFilterCombo.setForeground(UIHelper.getInputTextColor());
        searchControls.add(genreFilterCombo);

        searchBtn = UIHelper.createCustomButton("Search", UIHelper.COLOR_PRIMARY, Color.WHITE);
        searchBtn.setPreferredSize(new Dimension(90, 32));
        searchBtn.addActionListener(e -> performSearch());
        searchControls.add(searchBtn);

        resetBtn = UIHelper.createSecondaryButton("Reset");
        resetBtn.setPreferredSize(new Dimension(80, 32));
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            genreFilterCombo.setSelectedIndex(0);
            loadMoviesToTable(null, null);
        });
        searchControls.add(resetBtn);

        UIHelper.linkEnterKeyToButton(searchField, searchBtn);
        toolbarPanel.add(searchControls, BorderLayout.WEST);

        JPanel actionControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        actionControls.setOpaque(false);

        JButton addMovieBtn = UIHelper.createCustomButton("+ Add Movie", UIHelper.COLOR_SUCCESS, Color.WHITE);
        addMovieBtn.setPreferredSize(new Dimension(125, 32));
        addMovieBtn.addActionListener(e -> openAddMovieDialog());
        actionControls.add(addMovieBtn);

        if (currentUser.canManageUsers()) {
            JButton manageUsersBtn = UIHelper.createCustomButton("Manage Users", UIHelper.COLOR_PRIMARY, Color.WHITE);
            manageUsersBtn.setPreferredSize(new Dimension(135, 32));
            manageUsersBtn.addActionListener(e -> openUserManagement());
            actionControls.add(manageUsersBtn);
        }

        toolbarPanel.add(actionControls, BorderLayout.EAST);
        topContainer.add(toolbarPanel, BorderLayout.SOUTH);
        mainPanel.add(topContainer, BorderLayout.NORTH);

        String[] columnNames = {"S.No", "Title", "Genre", "Rating", "Rewatchable", "Your Review"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        movieTable = new JTable(tableModel);
        movieTable.setRowHeight(34);
        movieTable.setFont(UIHelper.FONT_REGULAR);
        movieTable.setBackground(UIHelper.getTableBgColor());
        movieTable.setForeground(UIHelper.getTableFgColor());
        movieTable.setGridColor(UIHelper.getTableGridColor());
        movieTable.setSelectionBackground(UIHelper.getTableSelectionBg());
        movieTable.setSelectionForeground(UIHelper.getTableSelectionFg());
        movieTable.getTableHeader().setFont(UIHelper.FONT_BOLD);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.setShowGrid(true);

        movieTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        movieTable.getColumnModel().getColumn(0).setMaxWidth(65);
        movieTable.getColumnModel().getColumn(1).setPreferredWidth(210);
        movieTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        movieTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        movieTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        movieTable.getColumnModel().getColumn(5).setPreferredWidth(370);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(table.getBackground());
                    lbl.setForeground(table.getForeground());
                }
                return lbl;
            }
        };
        movieTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        movieTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer standardRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(table.getBackground());
                    lbl.setForeground(table.getForeground());
                }
                return lbl;
            }
        };
        movieTable.getColumnModel().getColumn(1).setCellRenderer(standardRenderer);
        movieTable.getColumnModel().getColumn(2).setCellRenderer(standardRenderer);
        movieTable.getColumnModel().getColumn(5).setCellRenderer(standardRenderer);

        DefaultTableCellRenderer ratingRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UIHelper.FONT_STAR);
                if (isSelected) {
                    lbl.setBackground(table.getSelectionBackground());
                    lbl.setForeground(table.getSelectionForeground());
                } else {
                    lbl.setBackground(table.getBackground());
                    lbl.setForeground(new Color(230, 126, 34));
                }
                return lbl;
            }
        };
        movieTable.getColumnModel().getColumn(3).setCellRenderer(ratingRenderer);

        movieTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(UIHelper.getTableHeaderBg());
                lbl.setForeground(UIHelper.getTableHeaderFg());
                lbl.setFont(UIHelper.FONT_BOLD);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, UIHelper.getTableGridColor()),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                return lbl;
            }
        });

        movieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && movieTable.getSelectedRow() != -1) {
                    openMovieDetails();
                }
            }
        });

        scrollPane = new JScrollPane(movieTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIHelper.getBorderColor(), 1));
        scrollPane.getViewport().setBackground(UIHelper.getTableBgColor());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        bottomContainer = new JPanel(new BorderLayout(15, 6));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(6, 4, 4, 4));

        statusLabel = new JLabel("Showing: 0 movie(s)");
        statusLabel.setFont(UIHelper.FONT_REGULAR);
        statusLabel.setForeground(UIHelper.getTextColor());
        statusLabel.setPreferredSize(new Dimension(240, 30));
        bottomContainer.add(statusLabel, BorderLayout.WEST);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonGroup.setOpaque(false);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh Collection");
        refreshBtn.addActionListener(e -> refreshMovieList());
        buttonGroup.add(refreshBtn);

        JButton viewDetailsBtn = UIHelper.createCustomButton("View Movie Details", UIHelper.COLOR_PRIMARY, Color.WHITE);
        viewDetailsBtn.addActionListener(e -> openMovieDetails());
        buttonGroup.add(viewDetailsBtn);

        bottomContainer.add(buttonGroup, BorderLayout.EAST);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        String selectedGenre = (String) genreFilterCombo.getSelectedItem();

        if ("All Genres".equals(selectedGenre)) {
            loadMoviesToTable(keyword, null);
        } else {
            loadMoviesToTable(keyword, selectedGenre);
        }
    }

    public void loadMoviesToTable(String titleKeyword, String genreKeyword) {
        tableModel.setRowCount(0);
        currentDisplayedMovies.clear();

        try {
            List<Movie> movies;

            if (genreKeyword != null && !genreKeyword.isEmpty() && !"All Genres".equals(genreKeyword)) {
                movies = dbManager.searchMovies(currentUser.getUserId(), titleKeyword != null ? titleKeyword : "", genreKeyword);
            } else if (titleKeyword != null && !titleKeyword.isEmpty()) {
                movies = dbManager.searchMovies(currentUser.getUserId(), titleKeyword);
            } else {
                movies = dbManager.getMoviesForUser(currentUser.getUserId());
            }

            currentDisplayedMovies.addAll(movies);

            int serialNo = 1;
            for (Movie movie : movies) {
                String ratingDisplay = (movie.getAverageRating() > 0.0)
                        ? "★ " + movie.getFormattedRating()
                        : "No rating";

                String rewatchDisplay = movie.isRewatchable() ? "Yes" : "No";

                String reviewDisplay = movie.getUserReview();
                if (reviewDisplay == null || reviewDisplay.trim().isEmpty()) {
                    reviewDisplay = "(No written review)";
                }

                tableModel.addRow(new Object[]{
                        serialNo++,
                        movie.getTitle(),
                        movie.getGenre(),
                        ratingDisplay,
                        rewatchDisplay,
                        reviewDisplay
                });
            }

            statusLabel.setText("Showing: " + movies.size() + " movie(s)");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch movies: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshMovieList() {
        String keyword = searchField.getText().trim();
        String genre = (String) genreFilterCombo.getSelectedItem();
        loadMoviesToTable(keyword, genre);
    }

    private void openMovieDetails() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please click on a movie in the table first.",
                    "No Movie Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRow < currentDisplayedMovies.size()) {
            Movie selectedMovie = currentDisplayedMovies.get(selectedRow);
            new MovieDetailsFrame(this, selectedMovie, currentUser, dbManager).setVisible(true);
        }
    }

    private void openAddMovieDialog() {
        new AdminPanelFrame(this, currentUser, dbManager).setVisible(true);
    }

    private void openUserManagement() {
        new UserManagementFrame(this, currentUser, dbManager).setVisible(true);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?", "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame(dbManager).setVisible(true);
        }
    }
}
