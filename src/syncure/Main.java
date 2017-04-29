package syncure;

import syncure.core.Config;
import syncure.core.FileManager;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by mikonse on 29.04.2017.
 */
public class Main {

    public static void main(String[] args) {
        Config config = new Config(null);
        FileManager manager = new FileManager(config);

        Thread managerThread = new Thread(manager);
        managerThread.start();
    }
}
