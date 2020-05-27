import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.List;
import java.util.*;

public class TimesheetFrame extends JFrame
{
    public TimesheetFrame()
    {
        super("Табель");

        JTabbedPane departmentsTabbedPane = new JTabbedPane();

        try {
            Statement statement = DBConnector.getConnection().createStatement();

            ResultSet departments = statement.executeQuery("SELECT * FROM departments");

            departmentsTabbedPane.setTabPlacement(JTabbedPane.LEFT);

            while(departments.next())
            {
                JTabbedPane tabbedPane = getTabbedPane(departments.getInt("id"));
                departmentsTabbedPane.add(departments.getString("name"),
                       tabbedPane);
            }

            JPanel mainPanel = new JPanel(new BorderLayout());

            mainPanel.add(departmentsTabbedPane, BorderLayout.CENTER);

            JPanel buttonsPanel = new JPanel(new FlowLayout());

            JButton employeesAdminButton = new JButton("Администратор персонала");
            employeesAdminButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EmployeesAdmin.getInstance();
                    EmployeesAdmin.isVisible(true);
                }
            });

            JButton departmentsAdminButton = new JButton("Администратор министерств");
            departmentsAdminButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DepartmentsAdmin.getInstance();
                    DepartmentsAdmin.isVisible(true);
                }
            });

            JPanel codePanel = new JPanel();
            codePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel codeLabel = new JLabel("Код:");
            codePanel.add(codeLabel);
            codePanel.add(Box.createHorizontalStrut(12));

            String[] codes = {"Я", "Н", "В", "Рв", "Б", "К", "ОТ", "До", "Хд", "У", "Ож"};

            JComboBox codesBox = new JComboBox(codes);
            codesBox.setEditable(true);
            codePanel.add(codesBox);

            JButton timekeeperButton = new JButton("Изменить код");
            timekeeperButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTabbedPane pane = (JTabbedPane) departmentsTabbedPane.getComponentAt(departmentsTabbedPane.getSelectedIndex());
                    JScrollPane scrollPane = (JScrollPane) pane.getComponent(pane.getSelectedIndex());
                    JViewport viewport = scrollPane.getViewport();
                    JTable table = (JTable) viewport.getView();

                    int col = table.getSelectedColumn();
                    int row = table.getSelectedRow();

                    String columnName = table.getColumnName(col);

                    if(columnName.length() > 2)
                    {
                        JOptionPane.showMessageDialog(
                                mainPanel,
                                "Можно изменять только коды визитов.",
                                "Ошибка!",
                                JOptionPane.PLAIN_MESSAGE
                        );
                    }
                    else {
                        int id = Integer.parseInt((String) table.getValueAt(row, 2));

                        int year = 2020;
                        int month = pane.getSelectedIndex();
                        int day = Integer.parseInt(table.getColumnName(col));

                        GregorianCalendar calendar = new GregorianCalendar(year, month, day);

                        if(table.getValueAt(row, col) == null)
                        {
                            Timekeeper.addVisit(id, calendar.getTime(), codesBox.getSelectedItem().toString());
                        }
                        else {
                            Timekeeper.changeCode(id, calendar.getTime(), codesBox.getSelectedItem().toString());
                        }

                        table.setValueAt(codesBox.getSelectedItem().toString(), row, col);
                    }
                }
            });

            buttonsPanel.add(employeesAdminButton);
            buttonsPanel.add(departmentsAdminButton);

            buttonsPanel.add(codePanel);
            buttonsPanel.add(timekeeperButton);

            mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
            add(mainPanel);

            statement.close();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 400);
        setMinimumSize(new Dimension(1500,400));
        setVisible(true);
    }

    public JTabbedPane getTabbedPane(int departmentId)
    {
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

        JTabbedPane tabbedPane = new JTabbedPane();

        try {
            Statement statement = DBConnector.getConnection().createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM employees WHERE department_id = " + departmentId);

            ArrayList<Integer> employeesId = new ArrayList<>();

            while(resultSet.next())
            {
                employeesId.add(resultSet.getInt("id"));
            }

            for(int i = 1; i <= 12; i++)
            {
                JTable table = getTable(i, 2020, employeesId);
                JScrollPane scrollPane = new JScrollPane(table);

                tabbedPane.add(months[i - 1], scrollPane);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tabbedPane;
    }

    // Возвращает таблицу с данными о работниках за месяц
    public JTable getTable(int monthNumber, int yearNumber, List<Integer> employeesId)
    {
        Vector<String> columns = new Vector<>();
        columns.add("Имя");
        columns.add("Должность");
        columns.add("Табельный №");

        YearMonth yearMonth = YearMonth.of(yearNumber, monthNumber);
        int daysInMonth = yearMonth.lengthOfMonth();

        for(int day = 1; day <= daysInMonth; day++)
            columns.add(Integer.toString(day));

        columns.add("Итого");

        Vector<Vector<String>> employeesInformation = new Vector<>();

        employeesId.forEach((id) -> {
            employeesInformation.add(getRow(monthNumber, yearNumber, id));
        });

        JTable table = new JTable(employeesInformation, columns);

        TableColumnModel columnModel = table.getColumnModel();

        //name column width
        columnModel.getColumn(0).setPreferredWidth(150);
        columnModel.getColumn(0).setMinWidth(150);
        columnModel.getColumn(0).setMaxWidth(150);

        // position, id columns width
        for(int i = 1; i < 3; i++)
        {
            columnModel.getColumn(i).setPreferredWidth(100);
            columnModel.getColumn(i).setMinWidth(100);
            columnModel.getColumn(i).setMaxWidth(100);
        }

        //dates columns width
        for(int i = 3; i < columnModel.getColumnCount() - 1; i++)
        {
            columnModel.getColumn(i).setPreferredWidth(25);
            columnModel.getColumn(i).setMinWidth(25);
            columnModel.getColumn(i).setMaxWidth(25);
        }

        // last column width
        columnModel.getColumn(columnModel.getColumnCount() - 1).setPreferredWidth(75);
        columnModel.getColumn(columnModel.getColumnCount() - 1).setMinWidth(75);
        columnModel.getColumn(columnModel.getColumnCount() - 1).setMaxWidth(150);

        return table;
    }

    // Возвращает строку таблицы с данными о работнике за месяц
    public Vector<String> getRow(int monthNumber, int yearNumber, int employeeId)
    {
        Vector<String> row = new Vector<String>();

        String month = "", nextMonth = "", nextYear = "";

        if(monthNumber < 10) {
            month += "0" + monthNumber;
            nextYear += yearNumber;
            if(monthNumber == 9){
                monthNumber++;
                nextMonth += monthNumber;
            }
            else {
                monthNumber++;
                nextMonth += "0" + monthNumber;
            }
        }
        else {
            month += monthNumber;
            if (monthNumber == 12) {
                nextMonth += "01";
                nextYear += yearNumber + 1;
            }
            else {
                monthNumber++;
                nextMonth += monthNumber;
                nextYear += yearNumber;
            }
        }

        try{
            Statement statement = DBConnector.getConnection().createStatement();

            ResultSet set = statement.executeQuery("SELECT * FROM employees WHERE id = " + employeeId);

            if(set.next())
            {
                row.add(set.getString("full_name"));
                row.add(set.getString("position"));
                row.add(set.getString("id"));
            }

            set = statement.executeQuery("SELECT day_code FROM work_visits WHERE employee_id = " + employeeId + " " +
                    "AND date >= '" + yearNumber + "-" + month + "-01' " +
                    "AND date < '" + nextYear + "-" + nextMonth + "-01'");

            HashMap<String, Integer> codes = new HashMap<>();

            while(set.next())
            {
                String dayCode = set.getString("day_code");

                row.add(dayCode);

                Integer count = codes.get(dayCode);
                if(count == null)
                    codes.put(dayCode, 1);
                else
                    codes.put(dayCode, count + 1);
            }

            String total = "";
            boolean isFirst = true;
            for (String code: codes.keySet())
            {
                if (!isFirst)
                    total += ";";

                total += code + "(" + codes.get(code) + ")";
            }

            row.add(total);
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return row;
    }
}