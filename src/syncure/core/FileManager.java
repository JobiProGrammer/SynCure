package syncure.core;

import sun.reflect.generics.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.StandardWatchEventKinds;
import java.util.ArrayList;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileManager implements Runnable{

    private Tree local;
    private Tree remote;
    private Object lock = new Object();
    private Config config;

    public FileManager(Config config) {
        this.config = config;
        local = new Tree(config.getLocalDirectory(), lock);
        remote = new Tree(config.getDriveDirectory(), lock);
        Thread localThread = new Thread(local);
        Thread remoteThread = new Thread(remote);

        localThread.start();
        remoteThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                lock.wait();
            } catch (InterruptedException e) {

            }
            ArrayList<File> sources = local.compare(remote);
            ArrayList<File> targets = remote.compare(local);
            sync(sources, targets);
        }
    }

    public void sync(ArrayList<File> sources, ArrayList<File> targets, boolean syncAll) {

    }
}
