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
    JLabel label1 = new JLabel("Password");
    JLabel label2 = new JLabel("Local directory path");
    JLabel label3 = new JLabel("Online drive directory path");

    JTextField textField1 = new JTextField(10);
    JTextField textField2 = new JTextField(10);
    JTextField textField3 = new JTextField(10);

    JButton button = new JButton("Save changes");
    GridBagConstraints c = new GridBagConstraints();

    public static void main(String[] args)
    {
        new ConfigGUI();
    }

    public ConfigGUI(){
        super("Change your configuration");

        setSize(300, 400);
        setResizable(true);
        c.gridy = 0;
        label1.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(label1, c);
        p.add(textField1, c);

        c.gridy = 1;
        p.add(label2, c);
        p.add(textField2, c);

        c.gridy = 2;
        p.add(label3, c);
        p.add(textField3, c);

        c.gridy = 3;
        c.gridx = 0;
        p.add(button, c);
        add(p);



        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
