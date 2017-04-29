package syncure.gui;
import syncure.core.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystems;

/**
 * Minimal GUI for setting your password, local directory path and online drive path.
 * Offers no methods.
 */
public class ConfigGUI extends JPanel implements ActionListener{

    private Config config;
    JButton b = new JButton("Test");
    JLabel label1 = new JLabel("Password");
    JLabel label2 = new JLabel("Local directory path");
    JLabel label3 = new JLabel("Online drive directory path");

    JTextField textField1 = new JTextField(10);
    JTextField textField2 = new JTextField(10);
    JTextField textField3 = new JTextField(10);

    JButton button = new JButton("Save changes");
    GridBagConstraints c = new GridBagConstraints();


    public ConfigGUI(){
        super(new GridBagLayout());

        c.gridy = 0;
        label1.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label1, c);
        this.add(textField1, c);

        c.gridy = 1;
        this.add(label2, c);
        this.add(textField2, c);

        c.gridy = 2;
        this.add(label3, c);
        this.add(textField3, c);

        c.gridy = 3;
        c.gridx = 0;
        this.add(button, c);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(textField1.getText().length() > 1)
            config.setPassword(textField1.getText());
        if(textField2.getText().length() > 1)
            config.setLocalDirectory(FileSystems.getDefault().getPath(textField2.getText()));
        if(textField3.getText().length() > 1)
            config.setDriveDirectory(FileSystems.getDefault().getPath(textField3.getText()));
    }

}
