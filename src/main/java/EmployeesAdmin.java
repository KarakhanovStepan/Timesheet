import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class EmployeesAdmin extends JFrame
{
    private static EmployeesAdmin employeesAdmin;

    private EmployeesAdmin()
    {
        super("Администратор персонала");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBounds(10,10,10,10);

        // Таблица
        JTable employeesTable = getEmployeesTable();
        JScrollPane scrollPane = new JScrollPane(employeesTable);

        mainPanel.add(scrollPane);

        // Кнопки
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton insertButton = new JButton("Добавить");
        JButton changeButton = new JButton("Изменить");
        JButton deleteButton = new JButton("Удалить");

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getEmployeeForm(employeesTable);
            }
        });

        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(employeesTable.getSelectedRow() > 0) {
                    int id = Integer.parseInt((String) employeesTable.getValueAt(employeesTable.getSelectedRow(), 0));
                    String name = (String) employeesTable.getValueAt(employeesTable.getSelectedRow(), 1);
                    String position = (String) employeesTable.getValueAt(employeesTable.getSelectedRow(), 2);
                    String department = (String) employeesTable.getValueAt(employeesTable.getSelectedRow(), 3);

                    getEmployeeForm(id, name, position, department, employeesTable);
                }
                else
                {
                    JOptionPane.showMessageDialog(
                            mainPanel,
                            "Неверная строка");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = employeesTable.getSelectedRow();
                int dialogButton = JOptionPane.YES_NO_OPTION;
                JOptionPane.showConfirmDialog (mainPanel,
                        "Вы хотите удалить сотрудника\n" +
                                "№ " + employeesTable.getValueAt(selectedRow, 0) + "\n" +
                                "Имя: " + employeesTable.getValueAt(selectedRow,1) + "\n" +
                                "Должность: " + employeesTable.getValueAt(selectedRow,2) + "\n" +
                                "Место работы: " + employeesTable.getValueAt(selectedRow, 3),
                        "Warning",
                        dialogButton);

                if(dialogButton == JOptionPane.YES_OPTION){
                    try {
                        Statement statement = DBConnector.getConnection().createStatement();

                        statement.executeUpdate("DELETE FROM work_visits WHERE " +
                                "employee_id = " + Integer.parseInt((String) employeesTable.getValueAt(selectedRow, 0)));

                        statement.executeUpdate("DELETE FROM employees WHERE " +
                                "id = " + Integer.parseInt((String) employeesTable.getValueAt(selectedRow, 0)));

                        ((DefaultTableModel)employeesTable.getModel()).removeRow(selectedRow);

                        statement.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        buttonsPanel.add(insertButton);
        buttonsPanel.add(changeButton);
        buttonsPanel.add(deleteButton);

        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setSize(500, 400);
        setVisible(true);
    }

    private JTable getEmployeesTable()
    {
        Vector<String> columns = new Vector<>();
        columns.add("Табельный №");
        columns.add("ФИО");
        columns.add("Должность");
        columns.add("Департамент");

        Vector<Vector<String>> rows = new Vector<>();

        try {
            Statement statement1 = DBConnector.getConnection().createStatement();
            Statement statement2 = DBConnector.getConnection().createStatement();

            ResultSet employeesSet = statement1.executeQuery("SELECT * FROM employees");
            ResultSet departmentsSet = statement2.executeQuery("SELECT * FROM departments");

            HashMap<Integer, String> departments = new HashMap<>();

            while (departmentsSet.next())
            {
                departments.put(departmentsSet.getInt("id"), departmentsSet.getString("name"));
            }

            while(employeesSet.next()){
                Vector<String> row = new Vector<>();

                row.add(employeesSet.getString("id"));
                row.add(employeesSet.getString("full_name"));
                row.add(employeesSet.getString("position"));
                row.add(departments.get(employeesSet.getInt("department_id")));

                rows.add(row);
            }

            employeesSet.close();
            departmentsSet.close();
            statement1.close();
            statement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable employeesTable = new JTable(rows, columns);

        return employeesTable;
    }

    private JFrame getEmployeeForm(JTable table)
    {
        JFrame mainFrame = new JFrame("Данные работника");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel nameLabel = new JLabel("Имя:");
        namePanel.add(nameLabel);
        namePanel.add(Box.createHorizontalStrut(12));
        JTextField fullNameField = new JTextField(30);
        namePanel.add(fullNameField);

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel positionLabel = new JLabel("Должность:");
        positionPanel.add(positionLabel);
        positionPanel.add(Box.createHorizontalStrut(12));
        JTextField positionField = new JTextField(15);
        positionPanel.add(positionField);

        JPanel departmentPanel = new JPanel();
        departmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel departmentLabel = new JLabel("Департамент:");
        departmentPanel.add(departmentLabel);
        departmentPanel.add(Box.createHorizontalStrut(12));

        ArrayList< String> departments = new ArrayList<>();

        try {
            Statement statement = DBConnector.getConnection().createStatement();
            ResultSet departmentsSet = statement.executeQuery("SELECT * FROM departments");

            while (departmentsSet.next())
            {
                departments.add(departmentsSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JComboBox departmentsBox = new JComboBox(departments.toArray());
        departmentsBox.setEditable(true);
        departmentPanel.add(departmentsBox);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String query = "INSERT INTO employees (full_name, position, department_id) " +
                            "VALUES ('" + fullNameField.getText() + "', '" + positionField.getText() + "', " +
                            + (departmentsBox.getSelectedIndex() + 1) + ")";

                    PreparedStatement preparedStatement = DBConnector.getConnection()
                            .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                    preparedStatement.executeUpdate();

                    ResultSet resultSet = preparedStatement.getGeneratedKeys();

                    if(resultSet.next()) {
                        Vector<String> newEmployee = new Vector<>();

                        newEmployee.add(Integer.toString(resultSet.getInt(1)));
                        newEmployee.add(fullNameField.getText());
                        newEmployee.add(positionField.getText());
                        newEmployee.add(departmentsBox.getSelectedItem().toString());

                        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                        tableModel.addRow(newEmployee);

                    }

                    mainFrame.setVisible(false);

                    preparedStatement.close();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton clearButton = new JButton("Очистить");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullNameField.setText("");
                positionField.setText("");
            }
        });

        buttonsPanel.add(saveButton);
        buttonsPanel.add(clearButton);

        mainPanel.add(namePanel);
        mainPanel.add(positionPanel);
        mainPanel.add(departmentPanel);
        mainPanel.add(buttonsPanel);

        mainFrame.add(mainPanel);

        mainFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        mainFrame.setSize(400, 200);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        return mainFrame;
    }

    private JFrame getEmployeeForm(int id, String name, String position, String department, JTable table)
    {
        JFrame mainFrame = new JFrame("Данные работника");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel nameLabel = new JLabel("Имя:");
        namePanel.add(nameLabel);
        namePanel.add(Box.createHorizontalStrut(12));
        JTextField fullNameField = new JTextField(30);
        fullNameField.setText(name);
        namePanel.add(fullNameField);

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel positionLabel = new JLabel("Должность:");
        positionPanel.add(positionLabel);
        positionPanel.add(Box.createHorizontalStrut(12));
        JTextField positionField = new JTextField(15);
        positionField.setText(position);
        positionPanel.add(positionField);

        JPanel departmentPanel = new JPanel();
        departmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel departmentLabel = new JLabel("Департамент:");
        departmentPanel.add(departmentLabel);
        departmentPanel.add(Box.createHorizontalStrut(12));

        ArrayList<String> departments = new ArrayList<>();

        try {
            Statement statement = DBConnector.getConnection().createStatement();
            ResultSet departmentsSet = statement.executeQuery("SELECT * FROM departments");

            while (departmentsSet.next())
            {
                departments.add(departmentsSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JComboBox departmentsBox = new JComboBox(departments.toArray());
        departmentsBox.setSelectedIndex(departments.indexOf(department));
        departmentsBox.setEditable(true);
        departmentPanel.add(departmentsBox);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Statement statement = DBConnector.getConnection().createStatement();

                    statement.executeUpdate("UPDATE employees SET full_name = '" + fullNameField.getText() + "', " +
                            "position = '" + positionField.getText() + "', " +
                            "department_id = " + (departmentsBox.getSelectedIndex() + 1 ) + " " +
                            "WHERE id = " + id);

                    int selectedRow = table.getSelectedRow();
                    table.setValueAt(fullNameField.getText(), selectedRow, 1);
                    table.setValueAt(positionField.getText(), selectedRow, 2);
                    table.setValueAt(departmentsBox.getSelectedItem(), selectedRow, 3);

                    mainFrame.setVisible(false);

                    statement.close();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton clearButton = new JButton("Очистить");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fullNameField.setText("");
                positionField.setText("");
            }
        });

        buttonsPanel.add(saveButton);
        buttonsPanel.add(clearButton);

        mainPanel.add(namePanel);
        mainPanel.add(positionPanel);
        mainPanel.add(departmentPanel);
        mainPanel.add(buttonsPanel);

        mainFrame.add(mainPanel);

        mainFrame.setDefaultCloseOperation(HIDE_ON_CLOSE);
        mainFrame.setSize(400, 200);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);

        return mainFrame;
    }

    public static EmployeesAdmin getInstance()
    {
        if(employeesAdmin == null)
        {
            employeesAdmin = new EmployeesAdmin();
            return employeesAdmin;
        }
        else
            return employeesAdmin;
    }

    public static void isVisible(boolean b)
    {
        employeesAdmin.setVisible(b);
    }
}