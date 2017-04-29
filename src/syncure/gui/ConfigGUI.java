package syncure.gui;
import javax.swing.*;
import java.awt.*;

/**
 * Minimal GUI for setting your password, local directory path and online drive path.
 * Offers no methods.
 */
public class ConfigGUI extends JFrame{
    //private Config config;
    JPanel p = new JPanel(new GridBagLayout());
    JButton b = new JButton("Test");
    JLabel label1 = new JLabel("Label 1");
    JLabel label2 = new JLabel("Label 2");
    JLabel label3 = new JLabel("Label 3");

    JTextField textField1 = new JTextField(10);
    JTextField textField2 = new JTextField(10);
    JTextField textField3 = new JTextField(10);

    GridBagConstraints c = new GridBagConstraints();

    public static void main(String[] args)
    {
        new ConfigGUI();
    }

    public ConfigGUI(){
        super("Change your configuration");

        setSize(300, 400);
        setResizable(true);
        p.add(label1);
        p.add(textField1);
        p.add(label2);
        p.add(textField2);
        p.add(label3);
        add(p);



        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
