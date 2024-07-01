import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EmployeeFilter extends JFrame implements KeyListener {
    JLabel l1;
    JTextField tf;
    JTable table;
    DefaultTableModel model;

    EmployeeFilter() {
        l1 = new JLabel("Enter ID/Phone:");
        l1.setBounds(50, 50, 200, 30);
        tf = new JTextField();
        tf.setBounds(150, 50, 200, 30);
        tf.addKeyListener(this);
        model = new DefaultTableModel();
        model.addColumn("Emp ID");
        model.addColumn("Emp Name");
        model.addColumn("Emp Phone");
        model.addColumn("Emp City");
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 500, 300);
        add(l1);
        add(tf);
        add(scrollPane);
        setSize(600, 450);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
        String filter = "%" + tf.getText() + "%"; // Modify the filter for LIKE query
        model.setRowCount(0); // Clear the table
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "mca6");

            // Update the query to use LIKE for partial matches
            String query = "SELECT * FROM emp WHERE emp_id LIKE ? OR emp_phone LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, filter);
            pstmt.setString(2, filter);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String empID = rs.getString("emp_id");
                String empName = rs.getString("emp_name");
                String empPhone = rs.getString("emp_phone");
                String empCity = rs.getString("emp_city");
                model.addRow(new Object[]{empID, empName, empPhone, empCity});
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EmployeeFilter();
    }
}
