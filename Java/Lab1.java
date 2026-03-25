package eece4435.lab1;

/**
 *
 * @author Niel
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.net.URL;
import java.util.regex.Pattern;

public class Lab1 {

    public static void main(String[] args) {
        JFrame loginFrame = new JFrame("Bookstore Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(1280, 720);
        loginFrame.setLayout(new BorderLayout());

        URL imageUrl = Lab1.class.getClassLoader().getResource("nnmc.png");
        if (imageUrl == null) {
            System.out.println("Resource not found");
        } else {
            ImageIcon logoIcon = new ImageIcon(imageUrl);
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setPreferredSize(new Dimension(1280, 500));
            loginFrame.add(logoLabel, BorderLayout.NORTH);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(10, 10, 10, 10);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.anchor = GridBagConstraints.CENTER;

        JLabel userLabel = new JLabel("Username:");
        panel.add(userLabel, grid);

        grid.gridx = 1;
        JTextField userText = new JTextField(15);
        panel.add(userText, grid);

        grid.gridx = 0;
        grid.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        panel.add(passLabel, grid);

        grid.gridx = 1;
        JPasswordField passText = new JPasswordField(15);
        panel.add(passText, grid);

        grid.gridx = 0;
        grid.gridy = 2;
        grid.gridwidth = 2;
        grid.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Login");
        panel.add(loginButton, grid);
        loginFrame.add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.equals("admin") && password.equals("admin123")) {
                loginFrame.dispose();
                openBookstoreMainPage();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.");
            }
        });
        loginFrame.setVisible(true);
    }

    static void openBookstoreMainPage() {
        JFrame mainPageFrame = new JFrame("Bookstore");
        mainPageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPageFrame.setSize(1000, 700);
        mainPageFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0xFFE4C4)); // Light beige color for header
        JLabel headerLabel = new JLabel("Welcome to the NNMC Bookstore", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        JTable bookTable;
        DefaultTableModel tableModel;
        String[] columnNames = {"CRN", "Title", "Author", "Price", "ISBN"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addBookButton = new JButton("Add Book");
        addBookButton.setFont(new Font("Arial", Font.BOLD, 16));
        addBookButton.addActionListener(new AddBookAction(connectToDatabase(), tableModel));

        JButton removeBookButton = new JButton("Remove Book");
        removeBookButton.setFont(new Font("Arial", Font.BOLD, 16));
        removeBookButton.addActionListener(new RemoveBookAction(connectToDatabase(), tableModel, bookTable));

        buttonPanel.add(addBookButton);
        buttonPanel.add(removeBookButton);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0xF0F8FF));
        JLabel footerLabel = new JLabel("Northern New Mexico College | Contact us at bookstore@nnmc.com");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        mainPageFrame.add(mainPanel);
        mainPageFrame.setVisible(true);
    }

    static Connection connectToDatabase() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:C:/sqlite/books.db";  //db file path
            conn = DriverManager.getConnection(url);
            System.out.println("Connection Successful");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage());
        }
        return conn;
    }

    static void loadBookList(Connection conn, DefaultTableModel tableModel) {
        String sql = "SELECT crn, title, author, price, isbn FROM books";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("crn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getString("isbn")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading books: " + e.getMessage());
        }
    }

    static class AddBookAction implements ActionListener {
        private final Connection conn;
        private final DefaultTableModel tableModel;

        public AddBookAction(Connection conn, DefaultTableModel tableModel) {
            this.conn = conn;
            this.tableModel = tableModel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField crnField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField authorField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField isbnField = new JTextField();
            Object[] message = {
                "CRN:", crnField,
                "Title:", titleField,
                "Author:", authorField,
                "Price:", priceField,
                "ISBN:", isbnField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Add Book", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String crnText = crnField.getText();
                String title = titleField.getText();
                String author = authorField.getText();
                String priceText = priceField.getText();
                String isbn = isbnField.getText();

                if (!crnText.isEmpty() && !title.isEmpty() && !author.isEmpty() && !priceText.isEmpty() && !isbn.isEmpty()) {

                    if (containsNumbers(author)) {
                        JOptionPane.showMessageDialog(null, "Title and Author cannot contain numbers.");
                        return;
                    }
                    if (!crnText.matches("\\d+")) {
                        JOptionPane.showMessageDialog(null, "CRN must contain only digits.");
                        return;
                    }
                    if (!isbn.matches("\\d+")) {
                        JOptionPane.showMessageDialog(null, "ISBN must contain only digits.");
                        return;
                    }
                    
                    try {
                        int crn = Integer.parseInt(crnText);
                        double price = Double.parseDouble(priceText);

                        addBookToDatabase(conn, crn, title, author, price, isbn);
                        loadBookList(conn, tableModel);  // Refresh the table with updated data
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid price format.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "All fields must be filled.");
                }
            }
        }
        
        boolean containsNumbers(String text) {
            return Pattern.compile("\\d").matcher(text).find();
        }
        
        private void addBookToDatabase(Connection conn, int crn, String title, String author, double price, String isbn) {
            String sql = "INSERT INTO books (crn, title, author, price, isbn) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, crn);
                pstmt.setString(2, title);
                pstmt.setString(3, author);
                pstmt.setDouble(4, price);
                pstmt.setString(5, isbn);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book added successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error adding book: " + e.getMessage());
            }
        }
    }

    static class RemoveBookAction implements ActionListener {
        private final Connection conn;
        private final DefaultTableModel tableModel;
        private final JTable bookTable;

        public RemoveBookAction(Connection conn, DefaultTableModel tableModel, JTable bookTable) {
            this.conn = conn;
            this.tableModel = tableModel;
            this.bookTable = bookTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int crn = (int) tableModel.getValueAt(selectedRow, 0);
                removeBookFromDatabase(conn, crn);
                loadBookList(conn, tableModel);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a book to remove.");
            }
        }

        private void removeBookFromDatabase(Connection conn, int crn) {
            String sql = "DELETE FROM books WHERE crn = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, crn);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book removed successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error removing book: " + e.getMessage());
            }
        }
    }
}