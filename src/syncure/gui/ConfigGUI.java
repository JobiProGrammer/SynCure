package syncure.gui;
import syncure.core.Config;
import syncure.core.Crypto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

    //JPasswordField da dann als Punkte angezeigt wird
    JPasswordField passwordField;
    JTextField localDirField;
    JTextField driveDirField;

    JButton button = new JButton("Save changes");
    GridBagConstraints c = new GridBagConstraints();
    
    JButton picklocal = new JButton("Pick local drive");
    JButton pickdrive = new JButton("Pick public drive");


    public ConfigGUI(Config config){
        super(new GridBagLayout());
        this.config = config;

        passwordField = new JPasswordField(10);
        
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
        this.add(picklocal, c);
        //inner method actzion listen da die klasse schon einen hat
        picklocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File path;
				if(localDirField.getText().equals("")){
					path = Crypto.chooseDir(true);
				}else{
					path = Crypto.chooseDir(true, new File(localDirField.getText()));
				}
				if(path==null){
					return;
				}
				localDirField.setText(path.getAbsolutePath());
			}
		});
       

        c.gridy = 2;
        this.add(label3, c);
        this.add(driveDirField, c);
        this.add(pickdrive, c);
        pickdrive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File path;
				if(driveDirField.getText().equals("")){
					path = Crypto.chooseDir(true);
				}else{
					path = Crypto.chooseDir(true, new File(driveDirField.getText()));
				}
				
				if(path==null){
					return;
				}
				driveDirField.setText(path.getAbsolutePath());
				
				
			}
		});

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
    	//TODO getText depricated änderen
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
