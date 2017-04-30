package syncure.gui;

import syncure.core.Config;

import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

/**
 * Created by mikonse on 29.04.2017.
 */
public class Gui {

    private TrayIcon trayIcon;
    private SystemTray tray;
    private Config config;
    public JFrame frame;

    public Gui(Config config) {
        frame = createMainFrame("SynCure");
        this.config = config;

        JTabbedPane tabbedPane = new JTabbedPane();

        JComponent filePanel = new FileBrowserPanel();
        tabbedPane.addTab("File Browser", filePanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent configPanel = new ConfigGUI(this.config);
        tabbedPane.addTab("Settings", configPanel);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        frame.getContentPane().add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        frame.setVisible(true);
    }

    private JFrame createMainFrame(String name){
        JFrame frame = new JFrame(name);

        URL iconURL = getClass().getResource("/syncure/resource/Stuff.png");
        // iconURL is null when not found
        ImageIcon icon = new ImageIcon(iconURL);
        frame.setIconImage(icon.getImage());

        try{
            System.out.println("setting look and feel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            System.out.println("Unable to set LookAndFeel");
        }
        if(SystemTray.isSupported()){
            System.out.println("system tray supported");
            tray=SystemTray.getSystemTray();

            ActionListener exitListener= e -> {
                System.out.println("Exiting....");
                System.exit(0);
            };
            PopupMenu popup=new PopupMenu();
            MenuItem defaultItem=new MenuItem("Exit");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            defaultItem=new MenuItem("Open");
            defaultItem.addActionListener(e -> {
                frame.setVisible(true);
                frame.setExtendedState(JFrame.NORMAL);
            });
            popup.add(defaultItem);
            trayIcon=new TrayIcon(icon.getImage(), "SystemTray Demo", popup);
            trayIcon.setImageAutoSize(true);
        }else{
            System.out.println("system tray not supported");
        }
        frame.addWindowStateListener(e -> {
            if(e.getNewState()==Frame.ICONIFIED){
                try {
                    tray.add(trayIcon);
                    frame.setVisible(false);
                    System.out.println("added to SystemTray");
                } catch (AWTException ex) {
                    System.out.println("unable to add to tray");
                }
            }
            if(e.getNewState()==7){
                try{
                    tray.add(trayIcon);
                    frame.setVisible(false);
                    System.out.println("added to SystemTray");
                }catch(AWTException ex){
                    System.out.println("unable to add to system tray");
                }
            }
            if(e.getNewState()==Frame.MAXIMIZED_BOTH){
                tray.remove(trayIcon);
                frame.setVisible(true);
                System.out.println("Tray icon removed");
            }
            if(e.getNewState()==Frame.NORMAL){
                tray.remove(trayIcon);
                frame.setVisible(true);
                System.out.println("Tray icon removed");
            }
        });

        frame.setSize(500, 600);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        return frame;
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
}
