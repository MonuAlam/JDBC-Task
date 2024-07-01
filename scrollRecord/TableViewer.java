import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class TableViewer extends JFrame implements ActionListener {
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private JTextArea textArea;
    private JTextField tableNameField;
    private JButton previousButton;
    private JButton nextButton;

    public TableViewer() {
        super("Oracle Table Viewer");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Table Name: "));
        tableNameField = new JTextField(20);
        inputPanel.add(tableNameField);
        JButton displayButton = new JButton("Display");
        displayButton.addActionListener(this);
        inputPanel.add(displayButton);
        add(inputPanel, BorderLayout.NORTH);

        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        previousButton = new JButton("<<");
        nextButton = new JButton(">>");
        previousButton.addActionListener(this);
        nextButton.addActionListener(this);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void connectToDatabase() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521:xe";
            String username = "system";
            String password = "mca6";
            con = DriverManager.getConnection(url, username, password);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
            System.exit(1);
        }
    }

    private void displayTable(String tableName) {
        try {
            String query = "SELECT * FROM " + tableName;
            rs = stmt.executeQuery(query);
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No records found in the table.");
            } else {
                displayCurrentRecord();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch records from the table.");
        }
    }

    private void displayCurrentRecord() {
        try {
            textArea.setText("");
            ResultSetMetaData metaData = rs.getMetaData();
            int numColumns = metaData.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                textArea.append(metaData.getColumnName(i) + ": " + rs.getString(i) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to display record.");
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getActionCommand().equals("Display")) {
                String tableName = tableNameField.getText().trim();
                if (!tableName.isEmpty()) {
                    connectToDatabase();
                    displayTable(tableName);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a table name.");
                }
            } else if (e.getSource() == nextButton) {
                if (rs.next()) {
                    displayCurrentRecord();
                } else {
                    JOptionPane.showMessageDialog(this, "No more records.");
                }
            } else if (e.getSource() == previousButton) {
                if (rs.previous()) {
                    displayCurrentRecord();
                } else {
                    JOptionPane.showMessageDialog(this, "Already at the first record.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to navigate records.");
        }
    }

    public static void main(String[] args) {
        TableViewer viewer = new TableViewer();
        viewer.setVisible(true);
    }
}
