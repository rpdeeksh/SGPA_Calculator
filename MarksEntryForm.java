import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksEntryForm {
    private JPanel Heading;
    private JPanel USN;
    private JPanel Name;
    private JPanel CCS;
    private JPanel CN;
    private JPanel IML;
    private JPanel OOPJ;
    private JPanel ABC;
    private JPanel GENAI;
    private JPanel SC;
    private JPanel Actions;
    private JLabel heading_label;
    private JLabel usn_label;
    private JTextField USN_tf;
    private JLabel name_label;
    private JTextField name_tf;
    private JLabel ccs_label;
    private JComboBox<Integer> ccs_comboBox1;
    private JLabel CN_label;
    private JComboBox<Integer> cn_comboBox1;
    private JComboBox<Integer> iml_comboBox1;
    private JLabel IML_label;
    private JComboBox<Integer> oopj_comboBox1;
    private JLabel oopj_label;
    private JComboBox<Integer> ABC_comboBox2;
    private JComboBox<Integer> genai_comboBox1;
    private JLabel genai_label;
    private JLabel abc_label;
    private JComboBox<Integer> sc_comboBox1;
    private JLabel sc_label;
    private JButton SGPAButton;
    private JButton enterButton;
    private JPanel main_panel;
    private JButton enterMarksButton;
    private JButton deleteGradePointsButton;
    private JPanel designby;

    public MarksEntryForm() {
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usn = USN_tf.getText().trim();
                if (!usn.isEmpty()) {
                    if (isUSNPresent(usn)) {
                        String name = getNameFromDatabase(usn);
                        name_tf.setText(name);
                        name_tf.setEditable(false);
                        double sgpa = getSGPAFromDatabase(usn);
                        if (sgpa != -1) {
                            JOptionPane.showMessageDialog(main_panel, "SGPA: " + sgpa);
                        } else{
                            JOptionPane.showMessageDialog(main_panel, "Student Name: " + name + "\nPlease enter the grades.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(main_panel, "USN is not present in the database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(main_panel, "Please enter a USN.");
                }
            }
        });
        populateComboBoxes();
        enterMarksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usn = USN_tf.getText().trim();
                pushGradePointsToDatabase(usn);
            }
        });
        deleteGradePointsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usn = USN_tf.getText().trim();
                deleteGradePointsFromDatabase(usn);
            }
        });
        SGPAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usn = USN_tf.getText().trim();
                calculateAndUpdateSGPA(usn);
            }
        });

    }



    // Method to check if the entered USN is in a valid format
    private boolean isUSNPresent(String usn) {
        // JDBC URL, username, and password
        String url = "jdbc:mysql://localhost:3306/RVU";
        String username = "root";
        String password = "";

        // SQL query to check if the USN exists in the database
        String sql = "SELECT COUNT(*) AS count FROM student WHERE usn = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usn);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    // Method to get the name of the student from the database
    private String getNameFromDatabase(String usn) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String name = "";

        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Your_database_name", "root", "");

            // Prepare SQL statement
            String sql = "SELECT Name FROM your_table_name WHERE USN = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, usn);

            // Execute query
            resultSet = statement.executeQuery();

            // Get student name from result set
            if (resultSet.next()) {
                name = resultSet.getString("Name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return name;
    }

    private void populateComboBoxes() {
        // Define list of grades
        List<Integer> grades = new ArrayList<>();
        for (int i = 10; i >= 4; i--) {
            grades.add(i);
        }

        // Add grades to combo boxes
        ccs_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        cn_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        iml_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        oopj_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        ABC_comboBox2.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        genai_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
        sc_comboBox1.setModel(new DefaultComboBoxModel<>(grades.toArray(new Integer[0])));
    }


    private void pushGradePointsToDatabase(String usn) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Your_database_name", "root", "");

            // Prepare SQL statement to update grade points
            String sql = "UPDATE Your_table_name SET CCS = ?, CN = ?, IML = ?, OOPJ = ?, ABC = ?, GENAI = ?, SC = ? WHERE USN = ?";
            statement = connection.prepareStatement(sql);

            // Set grade points from combo boxes
            statement.setInt(1, (Integer) ccs_comboBox1.getSelectedItem());
            statement.setInt(2, (Integer) cn_comboBox1.getSelectedItem());
            statement.setInt(3, (Integer) iml_comboBox1.getSelectedItem());
            statement.setInt(4, (Integer) oopj_comboBox1.getSelectedItem());
            statement.setInt(5, (Integer) ABC_comboBox2.getSelectedItem());
            statement.setInt(6, (Integer) genai_comboBox1.getSelectedItem());
            statement.setInt(7, (Integer) sc_comboBox1.getSelectedItem());
            statement.setString(8, usn);

            // Execute update
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(main_panel, "Grade points updated successfully.");
            } else {
                JOptionPane.showMessageDialog(main_panel, "Failed to update grade points.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteGradePointsFromDatabase(String usn) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Your_database_name", "root", "");

            // Prepare SQL statement to delete grade points
            String sql = "UPDATE Your_table_name SET CCS = NULL, CN = NULL, IML = NULL, OOPJ = NULL, ABC = NULL, GENAI = NULL, SC = NULL, SGPA = NULL WHERE USN = ?";
            statement = connection.prepareStatement(sql);

            // Set USN parameter
            statement.setString(1, usn);

            // Execute update
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(main_panel, "Grade points deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(main_panel, "No grade points found for the provided USN.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private double getSGPAFromDatabase(String usn) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        double sgpa = -1; // Default value if SGPA is not found

        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Your_database_name", "root", "");

            // Prepare SQL statement
            String sql = "SELECT SGPA FROM Your_table_name WHERE USN = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, usn);

            // Execute query
            resultSet = statement.executeQuery();

            // Get SGPA from result set
            if (resultSet.next()) {
                sgpa = resultSet.getDouble("SGPA");
            }else {
                // If SGPA is not found, return -1
                sgpa = -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return sgpa;
    }


    public void calculateAndUpdateSGPA(String usn) {
        // Assuming you have the grade points and credit hours for each subject
        int ccsGrade = (Integer) ccs_comboBox1.getSelectedItem();
        int cnGrade = (Integer) cn_comboBox1.getSelectedItem();
        int imlGrade = (Integer) iml_comboBox1.getSelectedItem();
        int oopjGrade = (Integer) oopj_comboBox1.getSelectedItem();
        int abcGrade = (Integer) ABC_comboBox2.getSelectedItem();
        int genaiGrade = (Integer) genai_comboBox1.getSelectedItem();
        int scGrade = (Integer) sc_comboBox1.getSelectedItem();


        // Calculate total credit hours
        int totalCreditHours = 24;

        // Calculate total grade points
        int totalGradePoints = ccsGrade*4 + cnGrade*4 + imlGrade*4 + oopjGrade*4 + abcGrade*3 + genaiGrade*3 + scGrade*2;

        // Calculate SGPA
        double sgpa = (double) totalGradePoints / totalCreditHours;

        // Update SGPA in the database
        updateSGPAInDatabase(usn, sgpa);
    }

    private void updateSGPAInDatabase(String usn, double sgpa) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Establish connection to your database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Your_database_name", "root", "");

            // Prepare SQL statement to update SGPA
            String updateSql = "UPDATE Your_table_name SET SGPA = ? WHERE USN = ?";
            statement = connection.prepareStatement(updateSql);

            // Set SGPA and USN parameters
            statement.setDouble(1, sgpa);
            statement.setString(2, usn);

            // Execute update
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Prepare SQL statement to fetch student name
                String selectSql = "SELECT Name FROM Your_table_name WHERE USN = ?";
                statement = connection.prepareStatement(selectSql);
                statement.setString(1, usn);

                // Execute query to fetch student name
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    JOptionPane.showMessageDialog(main_panel, "SGPA updated successfully.\nCongratulations " + name + " !!✨\nYour SGPA is: " + sgpa);
                } else {
                    JOptionPane.showMessageDialog(main_panel, "Failed to fetch student's name.");
                }
            } else {
                JOptionPane.showMessageDialog(main_panel, "Failed to update SGPA.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }




    public static void main(String[] args) {
        JFrame frame = new JFrame("MarksEntryForm");
        frame.setContentPane(new MarksEntryForm().main_panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
