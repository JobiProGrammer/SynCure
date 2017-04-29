package syncure.core;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileManager implements Runnable {

    Tree localTree;
    Tree remoteTree;
    private Thread localTreeWatcher;
    private Thread remoteTreeWatcher;
    private final Object lock = new Object();
    private Config config;
    private boolean terminated;

    /**
     * Instantiates a FileManager
     * @param config global Config object
     */
    public FileManager(Config config) {
        this.config = config;
        this.terminated = false;
        localTree = new Tree(config.getLocalDirectory(), lock);
        remoteTree = new Tree(config.getDriveDirectory(), lock);

        //updated, checkt ob metadata noch nicht vorliegt und erstellt ggf und synchronisiert one way
        if (MetaData.isNew(config.getLocalDirectory()) && MetaData.isNew(config.getDriveDirectory())) {
            FileSync.copyDir(config.getLocalDirectory().toFile(),
                    config.getDriveDirectory().toFile(), true, config);
        }
        localTree.updateJson();
        remoteTree.updateJson();
        ToSync toSync = Tree.compare(localTree, remoteTree);
        sync(toSync.source, toSync.target, false);

        localTreeWatcher = new Thread(localTree);
        remoteTreeWatcher = new Thread(remoteTree);

        localTreeWatcher.start();
        remoteTreeWatcher.start();
    }

    @Override
    public void run() {
        while (!terminated) {
            try {
                synchronized (lock) {
                    lock.wait();  // wait for notifications from Directory Watcher Threads
                }
            } catch (InterruptedException e) {

            }
            ToSync toSync = Tree.compare(localTree, remoteTree);
            sync(toSync.source, toSync.target, false);

        }
    }

    /**
     * terminates all directory tree watcher Threads
     */
    public void terminateThreads() {
        localTreeWatcher.interrupt();
        remoteTreeWatcher.interrupt();
    }

    /**
     * terminates the Thread
     */
    public void terminate() {
        this.terminated = true;

        try {
            terminateThreads();
        } catch (Exception e) {

        }
    }

    /**
     * Synchronisiert die Dateien, die in der source list angeben sind, zu dem Pfad, der in Target ist.
     * Ob die Datei verschlüsselt oder entschlüsselt werden muss, stellt die Methode fest.
     * @param sources
     * @param targets
     * @param syncAll
     */
    public void sync(ArrayList<File> sources, ArrayList<File> targets, boolean syncAll) {
        if (syncAll) {
            FileSync.copyDir(config.getLocalDirectory().toFile(), config.getDriveDirectory().toFile(), true, config);
            return;
        }

        if (sources.size() != targets.size()) {
            return;
        }
        int len = sources.size();
        MetaData localMeta = new MetaData(config.getLocalDirectory());
        ArrayList<MetaFileObject> list= localMeta.getData();

        MetaData targetMeta =new MetaData(config.getDriveDirectory());
        ArrayList<MetaFileObject> list2= targetMeta.getData();

        for (int i = 0; i < len; i++) {
            if (isTarget(targets.get(i))) {
                if (sources.get(i).getAbsolutePath().contains(".aes")) {
                    System.out.println("Hier muss ein Fehler vorliegen: aes soll gerade verschlüsselt werden!?");
                }
                FileSync.copyFile(sources.get(i), targets.get(i), true, config);
            } else {
                FileSync.copyFile(sources.get(i), targets.get(i), false, config);
            }
            int lenL = list.size();
            long zeit=System.currentTimeMillis();
            for(int j=0; j<lenL; j++){
                if(list.get(j).path.equals(sources.get(i).getAbsolutePath())){
                    list.get(j).time=zeit;
                    localMeta.setData();
                    break;
                }
            }

            lenL = list2.size();
            for(int j=0; j<lenL; j++){
                if(list2.get(j).path.equals(sources.get(i).getAbsolutePath())){
                    list2.get(j).time=zeit;
                    targetMeta.setData();
                    return;
                }
            }
            list2.add(new MetaFileObject(targets.get(i).getAbsolutePath(), zeit));
            targetMeta.setData();
        }
    }

    private boolean isTarget(File f) {
        return f.getAbsolutePath().contains(config.getDriveDirectory().toAbsolutePath().toString());
    }
}
