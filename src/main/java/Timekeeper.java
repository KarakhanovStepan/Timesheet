import javax.swing.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class Timekeeper extends JFrame
{
    public static void addVisit(int id, Date date, String code)
    {
        DBConnector.visit(id, date, code);
    }

    public static void changeCode(int id, Date date, String code)
    {
        try {
            Statement statement = DBConnector.getConnection().createStatement();

            System.out.println("Changing code...");

            statement.executeUpdate("UPDATE work_visits SET day_code = '" + code + "'" +
                    "WHERE employee_id = " + id + " AND date = '" + new java.sql.Date(date.getTime()) + "'");

            System.out.println("Code changed!");

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}