import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeeViewer extends JFrame {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private JTextField idField, nameField, phoneField, cityField, recordCounterField;
    private int totalRecords = 0;
    private JScrollBar scrollBar;

    public EmployeeViewer() {
        super("Employee ScrollBar");
        setSize(400, 350); // Increase height to accommodate the scrollbar at the bottom
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        idField = new JTextField(5); 
        nameField = new JTextField(5);
        phoneField = new JTextField(5); 
        cityField = new JTextField(5); 
        recordCounterField = new JTextField(5);
        recordCounterField.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Record:"));
        panel.add(recordCounterField);

        try {
            // Connect to the Oracle database
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "mca6");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery("SELECT * FROM emp");

            resultSet.first(); // Move cursor to the first record
            resultSet.last();
            totalRecords = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayRecord(); // Display the first record by default

        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, totalRecords);
        scrollBar.setPreferredSize(new Dimension(200, 50)); // Set preferred size
        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                try {
                    resultSet.absolute(scrollBar.getValue() + 1);
                    displayRecord();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollBar, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	
    }

    private void displayRecord() {
        try {
            idField.setText(resultSet.getString("emp_id"));
            nameField.setText(resultSet.getString("emp_name"));
            phoneField.setText(resultSet.getString("emp_phone"));
            cityField.setText(resultSet.getString("emp_city"));
            recordCounterField.setText(resultSet.getRow() + "/" + totalRecords);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EmployeeViewer employeeViewer = new EmployeeViewer();
        employeeViewer.setVisible(true);
    }
}
