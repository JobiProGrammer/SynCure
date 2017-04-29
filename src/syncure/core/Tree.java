package syncure.core;


import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;


/**
 * Created by david on 29.04.17.
 */
public class Tree implements Runnable {

    private Path path;
    private Object lock;
    private boolean terminated;

    public Path getPath(){ return path; }


    public Tree(Path path, Object lock) {
        this.path = path;
        this.lock = lock;
        this.terminated = false;
    }

    public static ToSync compare(Tree local, Tree drive) {
        MetaData metaLocal = new MetaData(local.path);
        MetaData metaDrive = new MetaData(drive.path);
        ArrayList<MetaFileObject> localFiles = metaLocal.getData();
        ArrayList<MetaFileObject> driveFiles = metaDrive.getData();
        ToSync toSync = new ToSync();

        for(MetaFileObject mLocal : localFiles) {
            for(MetaFileObject mDrive : driveFiles) {
                if(!mDrive.path.contains(".aes")) {
                    driveFiles.remove(mDrive);
                    continue;
                }
                 else {
                    mDrive.path.replace(".aes", "");
                }
                if(mLocal.path.equals(mDrive.path)) {
                    if(mLocal.time < mDrive.time) {
                        toSync.add(new File(mDrive.path + ".aes"), new File(mLocal.path));
                        driveFiles.remove(mDrive);
                        localFiles.remove(mLocal);
                    } else if(mLocal.time > mDrive.time) {
                        toSync.add(new File(mLocal.path), new File(mDrive.path + ".aes"));
                        driveFiles.remove(mDrive);
                        localFiles.remove(mLocal);
                    }
                    break;
                }

            }
            toSync.add(new File(mLocal.path), new File(drive.path.toFile().getAbsolutePath() + mLocal.path.replace(local.path.toFile().getAbsolutePath(), "") + ".aes"));

        }
        for(MetaFileObject mDrive : driveFiles) {
            if(!mDrive.path.contains(".aes")) {
                continue;
            } else {
                toSync.add(new File(mDrive.path + ".aes"), new File(local.path.toFile().getAbsolutePath() + mDrive.path.replace(drive.path.toFile().getAbsolutePath(), "")));
            }
        }

        return toSync;
    }

    public void run() {
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path, "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path
                        + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

            // Start the infinite polling loop
            WatchKey key = null;
            while (!this.terminated) {
                key = service.take();

                    synchronized (lock) {
                        updateJson();
                        lock.notify();
                    }

                if (!key.reset()) {
                    break; // loop
                }
            }

        } catch (IOException ioe) {

        } catch (InterruptedException ie) {

        }

    }


    /**
     * Updates the jason file when chanes happened
     */
    private void updateJson(){
    	MetaData md = new MetaData(path);
    	md.writeinitFiles();
    }
    public void terminate() {
        this.terminated = true;
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        // Folder we are going to watch
        // Path folder =
        // Paths.get(System.getProperty("C:\\Users\\Isuru\\Downloads"));

        new Thread(new Tree(FileSystems.getDefault().getPath("Test"), new Object())).start();

    }
}
