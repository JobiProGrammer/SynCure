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
    JLabel label4 = new JLabel("Please restart the Application for the changes to apply");

    JTextField passwordField;
    JTextField localDirField;
    JTextField driveDirField;

    JButton button = new JButton("Save changes");
    GridBagConstraints c = new GridBagConstraints();


    public ConfigGUI(Config config){
        super(new GridBagLayout());
        this.config = config;

        passwordField = new JTextField(10);
        localDirField = new JTextField(10);
        localDirField.setText(this.config.getLocalDirectory().toAbsolutePath().toString());
        driveDirField = new JTextField(10);
        driveDirField.setText(this.config.getDriveDirectory().toAbsolutePath().toString());

        c.gridy = 0;
        label1.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label1, c);
        this.add(passwordField, c);

        c.gridy = 1;
        this.add(label2, c);
        this.add(localDirField, c);

        c.gridy = 2;
        this.add(label3, c);
        this.add(driveDirField, c);

        c.gridy = 3;
        c.gridx = 0;
        this.add(button, c);
        button.addActionListener(this);

        c.gridy = 4;
        label4.setVisible(false);
        this.add(label4, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(passwordField.getText().length() > 1)
            config.setPassword(passwordField.getText());
        if(localDirField.getText().length() > 1)
            config.setLocalDirectory(FileSystems.getDefault().getPath(localDirField.getText()));
        if(driveDirField.getText().length() > 1)
            config.setDriveDirectory(FileSystems.getDefault().getPath(driveDirField.getText()));
        config.setConfig();
        label4.setVisible(true);
    }

}
