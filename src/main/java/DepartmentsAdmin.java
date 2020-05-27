import javax.swing.*;
import java.awt.*;

public class DepartmentsAdmin extends JFrame
{
    private static DepartmentsAdmin departmentsAdmin;

    private DepartmentsAdmin()
    {
        super("Администратор департаментов");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new Button("Departments"));

        add(panel);

        setSize(500, 400);
        setVisible(true);
    }

    public static DepartmentsAdmin getInstance()
    {
        if(departmentsAdmin == null)
        {
            departmentsAdmin = new DepartmentsAdmin();
            return departmentsAdmin;
        }
        else
            return departmentsAdmin;
    }

    public static void isVisible(boolean b)
    {
        departmentsAdmin.setVisible(b);
    }
}
