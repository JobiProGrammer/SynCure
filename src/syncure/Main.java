package syncure;

import syncure.core.Config;
import syncure.core.FileManager;
import syncure.gui.ConfigGUI;
import syncure.gui.Gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by mikonse on 29.04.2017.
 */
public class Main {

    public static void main(String[] args) {
        Config config = new Config(null);
        FileManager manager = new FileManager(config);

        Thread managerThread = new Thread(manager);
        managerThread.start();

        Gui gui = new Gui(config);
        gui.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                manager.terminate();
            }
        });
    }
}
