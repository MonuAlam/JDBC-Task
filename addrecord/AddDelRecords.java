import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AddDelRecords extends JFrame implements ActionListener {
    private JTextField idField, nameField, phoneField, cityField;
    private JButton addButton, deleteButton;
    private Connection conn;
    private DefaultTableModel tableModel;
    private JTable dataTable;

    public AddDelRecords() {
        super("Database GUI");

        // Initialize components
        idField = new JTextField(10);
        nameField = new JTextField(20);
        phoneField = new JTextField(15);
        cityField = new JTextField(20);
        addButton = new JButton("Add Record");
        deleteButton = new JButton("Delete Record");

        // Add action listeners
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);

        // Create table model
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);

        // Set table column headers
        tableModel.addColumn("Emp ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Phone");
        tableModel.addColumn("City");

        // Set layout
        setLayout(new BorderLayout());

        // Add components to the frame
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Emp ID: "));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name: "));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone: "));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("City: "));
        inputPanel.add(cityField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Connect to the database
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "mca6");
            loadTableData(); // Load initial table data
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set frame properties
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addRecord();
        } else if (e.getSource() == deleteButton) {
            deleteRecord();
        }
    }

    private void addRecord() {
        String empId = idField.getText();
        String name = nameField.getText();
        String phone = phoneField.getText();
        String city = cityField.getText();

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO emp (emp_id, emp_name, emp_phone, emp_city) VALUES (?, ?, ?, ?)");
            stmt.setString(1, empId);
            stmt.setString(2, name);
            stmt.setString(3, phone);
            stmt.setString(4, city);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record added successfully!");
            loadTableData(); // Reload table data after adding record
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding record!");
        }
    }

    private void deleteRecord() {
        String empId = idField.getText();

        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM emp WHERE emp_id = ?");
            stmt.setString(1, empId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!");
                loadTableData(); // Reload table data after deleting record
            } else {
                JOptionPane.showMessageDialog(this, "Record not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting record!");
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0); // Clear existing table data
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM emp");
            while (rs.next()) {
                String empId = rs.getString("emp_id");
                String name = rs.getString("emp_name");
                String phone = rs.getString("emp_phone");
                String city = rs.getString("emp_city");
                tableModel.addRow(new Object[]{empId, name, phone, city});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AddDelRecords();
    }
}
