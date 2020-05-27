import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DBConnector
{
    private static Connection connection;

    public void createDataBase(String url, String user, String password, String dbName)
    {
        try {
            connection = DriverManager.getConnection(url, user, password);

            Statement statement = connection.createStatement();

            statement.executeUpdate("DROP DATABASE IF EXISTS " + dbName + "");

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + "");
            statement.executeUpdate("USE " + dbName);

            statement.executeUpdate("CREATE TABLE `" + dbName + "`.`departments` (\n" +
                    "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `name` VARCHAR(100) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`))");

            statement.executeUpdate("CREATE TABLE `" + dbName + "`.`employees` (\n" +
                    "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `full_name` VARCHAR(45) NOT NULL,\n" +
                    "  `position` VARCHAR(45) NOT NULL,\n" +
                    "  `department_id` INT NOT NULL,\n" +
                    "  FOREIGN KEY (department_id)  REFERENCES departments (id),\n" +
                    "  PRIMARY KEY (`id`))");

            statement.executeUpdate("CREATE TABLE `" + dbName + "`.`calendar` (\n" +
                    "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `date` DATE NOT NULL,\n" +
                    "  `holiday` TINYINT NOT NULL,\n" +
                    "  `pre_holiday` TINYINT NOT NULL,\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  UNIQUE INDEX `date_UNIQUE` (`date` ASC) VISIBLE)");

            statement.executeUpdate("CREATE TABLE `" + dbName + "`.`days_codes` (\n" +
                    "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `code` VARCHAR(4) NOT NULL,\n" +
                    "  `description` VARCHAR(200) NOT NULL,\n" +
                    "  PRIMARY KEY (`id`))");

            statement.executeUpdate("CREATE TABLE `" + dbName + "`.`work_visits` (\n" +
                    "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
                    "  `employee_id` INT NOT NULL,\n" +
                    "  `date` DATE NOT NULL,\n" +
                    "  `day_code` VARCHAR(4) NOT NULL,\n" +
                    "  FOREIGN KEY (employee_id)  REFERENCES employees (id),\n" +
                    "  FOREIGN KEY (date)  REFERENCES calendar (date),\n" +
                    "  PRIMARY KEY (`id`))");

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createCalendar(int year, ArrayList<java.sql.Date> holidays, ArrayList<java.sql.Date> preHolidays,
                               ArrayList<java.sql.Date> workingHolidays)
    {
        GregorianCalendar calendar = new GregorianCalendar(year, Calendar.JANUARY, 1);

        try {

            for(int i = 0; i < (calendar.isLeapYear(year) ? 366 : 365); i++)
            {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO calendar (date, holiday, pre_holiday) VALUES (?,?,?)");

                java.sql.Date sqlDate = new java.sql.Date(calendar.getTime().getTime());

                preparedStatement.setDate(1, sqlDate, calendar);

                if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                        || holidays.contains(sqlDate) && !workingHolidays.contains(sqlDate))
                    preparedStatement.setInt(2, 1);
                else
                    preparedStatement.setInt(2, 0);

                if(preHolidays.contains(sqlDate))
                    preparedStatement.setInt(3, 1);
                else
                    preparedStatement.setInt(3,0);

                preparedStatement.execute();

                calendar.add(Calendar.DATE, 1);

                preparedStatement.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDayCode(String code, String description)
    {
        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO days_codes (code, description) " +
                    "VALUES ('" + code + "','" + description + "')");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertDepartment(String name)
    {
        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO departments (name) VALUES ('" + name + "')");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertEmployee(String fullName, String position, int departmentId)
    {
        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO employees (full_name, position, department_id) " +
                    "VALUES ('" + fullName + "','" + position + "'," + departmentId + ")");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void visit(int id, java.util.Date date, String code)
    {
        try {
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO work_visits (employee_id, date, day_code) " +
                    "VALUES ('" + id + "','" + new java.sql.Date(date.getTime()) + "','" + code + "')");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getEmployeeName(int id)
    {
        String name = "NULL";

        try {
            Statement statement = connection.createStatement();

            ResultSet set = statement.executeQuery("SELECT full_name FROM employees WHERE id = " + id);
            if(set.next())
            {
                name = set.getString("full_name");
            }

            set.close();
            statement.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return name;
    }

    public static Connection getConnection()
    {
        return connection;
    }

    public void closeConnection()
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}