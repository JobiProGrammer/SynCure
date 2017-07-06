package syncure.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;

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
        	System.out.println("alles sync");
            FileSync.copyDir(config.getLocalDirectory().toFile(),
                    config.getDriveDirectory().toFile(), true, config);

        } else {
            localTree.updateJson();
            remoteTree.updateJson();
        }
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
            synchronized (lock) {
                try {
                    lock.wait();  // wait for notifications from Directory Watcher Threads
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (Tree.deleted.size() > 0) {
                    LinkedList<Path> toRemove = new LinkedList<>();
                    for (Path del : Tree.deleted) {
                        Path corresponding = getCorrespondingPath(del);
                        try {
                            deleteRecursive(corresponding.toFile());
                            //Files.delete(corresponding);
                        } catch (IOException e) {

                        }
                        toRemove.add(del);
                        localTree.metaData.readOrUpdate();
                    }
                    Tree.deleted.removeAll(toRemove);
                } else {
                    ToSync toSync = Tree.compare(localTree, remoteTree);
                    sync(toSync.source, toSync.target, false);
                }
            }
        }
    }

    private static void deleteRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                if (childFile.isDirectory()) {
                    deleteRecursive(childFile);
                } else {
                    if (!childFile.delete()) {
                        throw new IOException();
                    }
                }
            }
        }

        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * terminates all directory tree watcher Threads
     */
    public void terminateThreads() {
        localTree.terminate();
        remoteTree.terminate();
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
       
        MetaData localMeta;
        MetaData targetMeta;
        
        
        for (int i = 0; i < len; i++) {
            if (isTarget(targets.get(i))) {
                if (sources.get(i).getAbsolutePath().contains(".aes")) {
                    System.out.println("Hier muss ein Fehler vorliegen: aes soll gerade verschlüsselt werden!?");
                }
                FileSync.copyFile(sources.get(i), targets.get(i), true, config);
                //localMeta = new MetaData(config.getLocalDirectory());
                //targetMeta =new MetaData(config.getDriveDirectory());
            } else {
                FileSync.copyFile(sources.get(i), targets.get(i), false, config);
                //localMeta = new MetaData(config.getDriveDirectory());
                //targetMeta =new MetaData(config.getLocalDirectory());
            }
            
//            ArrayList<MetaFileObject> list= localMeta.getData();
//            ArrayList<MetaFileObject> list2= targetMeta.getData();
//            int lenL = list.size();
//            long zeit=System.currentTimeMillis();
//            for(int j=0; j<lenL; j++){
//                if(list.get(j).path.equals(sources.get(i).getAbsolutePath())){
//                    list.get(j).time=zeit;
//                    localMeta.setData();
//                    break;
//                }
//            }
//
//            lenL = list2.size();
//            for(int j=0; j<lenL; j++){
//                if(list2.get(j).path.equals(targets.get(i).getAbsolutePath())){
//                    list2.get(j).time=zeit;
//                    targetMeta.setData();
//                    return;
//                }
//            }
//            list2.add(new MetaFileObject(targets.get(i).getAbsolutePath(), zeit));
//            targetMeta.setData();
        }
    }

    public Path getCorrespondingPath(Path path) {
        if (path.toAbsolutePath().toString().contains(config.getDriveDirectory().toFile().getAbsolutePath())) {
            if (!path.toFile().isDirectory()) {
                return Paths.get(path.toAbsolutePath().toString().replace(
                        config.getDriveDirectory().toFile().getAbsolutePath(),
                        config.getLocalDirectory().toFile().getAbsolutePath()).replace(".aes", ""));
            } else {
                return Paths.get(path.toAbsolutePath().toString().replace(
                        config.getDriveDirectory().toFile().getAbsolutePath(),
                        config.getLocalDirectory().toFile().getAbsolutePath()));
            }
        } else if (path.toAbsolutePath().toString().contains(config.getLocalDirectory().toFile().getAbsolutePath())) {
            if (!path.toFile().isDirectory()) {
                return Paths.get(path.toAbsolutePath().toString().replace(
                        config.getLocalDirectory().toFile().getAbsolutePath(),
                        config.getDriveDirectory().toFile().getAbsolutePath()) + ".aes");
            } else {
                return Paths.get(path.toAbsolutePath().toString().replace(
                        config.getLocalDirectory().toFile().getAbsolutePath(),
                        config.getDriveDirectory().toFile().getAbsolutePath()));
            }
        }
        return null;
    }

    private boolean isTarget(File f) {
        return f.getAbsolutePath().contains(config.getDriveDirectory().toFile().getAbsolutePath());
    }
}
