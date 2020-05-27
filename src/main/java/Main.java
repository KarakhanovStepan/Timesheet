import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main
{


    public static void main(String[] args)
    {
        String url = "jdbc:mysql://localhost:3306?serverTimezone=UTC";
        String user = "root";
        String password = "rootroot";
        String dbName = "timesheet";

        DBConnector connector = new DBConnector();

        connector.createDataBase(url, user, password, dbName);

        fillDatabase(connector);

        new TimesheetFrame();
    }

    public static void fillDatabase(DBConnector connector)
    {
        // Праздники, предпраздничные дни и рабочие вфходные в 2020 году

        ArrayList<Date> holidays = new ArrayList<Date>();

        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,1).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,2).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,3).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,6).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,7).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JANUARY,8).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.FEBRUARY,24).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.MARCH,9).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.MAY,1).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.MAY,4).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.MAY,5).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.MAY,11).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.JUNE,12).getTime().getTime()));
        holidays.add(new java.sql.Date(new GregorianCalendar(2020,Calendar.NOVEMBER,4).getTime().getTime()));

        ArrayList<java.sql.Date> preHolidays = new ArrayList<Date>();

        preHolidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.APRIL, 30).getTime().getTime()));
        preHolidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.MAY, 8).getTime().getTime()));
        preHolidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.JUNE, 11).getTime().getTime()));
        preHolidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.NOVEMBER, 3).getTime().getTime()));
        preHolidays.add(new java.sql.Date(new GregorianCalendar(2020, Calendar.DECEMBER, 31).getTime().getTime()));

        ArrayList<java.sql.Date> workingHolidays = new ArrayList<Date>();

        connector.createCalendar(2020, holidays, preHolidays, workingHolidays);

        // Коды

        connector.insertDayCode("Я", "полный рабочий день");
        connector.insertDayCode("Н", "отсутствие на рабочее место по невыясненным причинам");
        connector.insertDayCode("В", "выходные и праздничные дни");
        connector.insertDayCode("Рв", "работа в праздничные и выходные дни; а также" +
                " работа в праздничные и выходные дни, при нахождении в командировке");
        connector.insertDayCode("Б", "дни временной нетрудоспособности");
        connector.insertDayCode("К", "командировочные дни; а также, выходные (нерабочие) дни при нахождении в командировке,\n" +
                "когда сотрудник отдыхает, в соответствии с графиком работы ООО «Наука» в командировке;");
        connector.insertDayCode("ОТ", "ежегодный основной оплаченный отпуск");
        connector.insertDayCode("До", "неоплачиваемый отпуск (отпуск за свой счет)");
        connector.insertDayCode("Хд", "хозяйственный день");
        connector.insertDayCode("У", "отпуск на период обучения");
        connector.insertDayCode("Ож", "отпуск по уходу за ребенком");

        GregorianCalendar calendar = new GregorianCalendar(2020, Calendar.JANUARY, 1);


        connector.insertDepartment("Департамент труда");
        connector.insertDepartment("Департамент патриотизма");
        connector.insertDepartment("Департамент науки");

        for(int i = 0; i < 10; i++) {
            connector.insertEmployee("Иванов Петр", "Работник", 1);
            connector.insertEmployee("Иванов Максим", "Работник", 1);
            connector.insertEmployee("Сидоров Петр", "Работник", 2);
            connector.insertEmployee("Сидоров Максим", "Работник", 2);
            connector.insertEmployee("Петров Петр", "Работник", 3);
            connector.insertEmployee("Петров Максим", "Работник", 3);
        }

        for(int i = 0; i < 366; i++)
        {
            DBConnector.visit(1, calendar.getTime(), "Я");
            DBConnector.visit(2, calendar.getTime(), "Н");
            DBConnector.visit(3, calendar.getTime(), "В");
            DBConnector.visit(4, calendar.getTime(), "Б");
            DBConnector.visit(5, calendar.getTime(), "К");
            DBConnector.visit(6, calendar.getTime(), "Ож");
            calendar.add(Calendar.DATE, 1);
        }
    }
}
