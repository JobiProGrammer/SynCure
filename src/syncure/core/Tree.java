package syncure.core;


import com.sun.nio.file.ExtendedWatchEventModifier;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by david on 29.04.17.
 */
public class Tree implements Runnable {

    private Path path;
    private Object lock;
    private boolean terminated;
    public MetaData metaData;

    public static LinkedList<Path> deleted = new LinkedList<>();

    public Tree(Path path, Object lock) {
        this.path = path;
        this.lock = lock;
        this.terminated = false;

        this.metaData = new MetaData(path);
    }

    public Path getPath(){ return path; }

    public static ToSync compare(Tree local, Tree drive) {
        ArrayList<MetaFileObject> localFiles = local.metaData.readOrUpdate();
        ArrayList<MetaFileObject> driveFiles = drive.metaData.readOrUpdate();
        ToSync toSync = new ToSync();

        boolean found = false;
        for(MetaFileObject mLocal : localFiles) {
            for(MetaFileObject mDrive : driveFiles) {
                if(!mDrive.path.contains(".aes")) {
                    continue;
                }
                if(mLocal.path.equals(mDrive.path.replace(".aes", ""))) {
                    if(mLocal.time < mDrive.time) {
                        System.out.println("Drive neuer");
                        System.out.println("Local time: " + mLocal.time);
                        System.out.println("Driver time: " + mDrive.time);
                        System.out.println(mDrive.path + " -> " + mLocal.path);
                        toSync.add(new File(mDrive.path), new File(mLocal.path));
                    } else if(mLocal.time > mDrive.time) {
                        System.out.println("Local neuer");
                        System.out.println("Local time: " + mLocal.time);
                        System.out.println("Driver time: " + mDrive.time);
                        System.out.println(mLocal.path + " -> " + mDrive.path);
                        toSync.add(new File(mLocal.path), new File(mDrive.path));
                    }
                    found = true;
                    break;
                }
            }

            if (!found) {
                toSync.add(new File(mLocal.path), new File(drive.path.toFile().getAbsolutePath() + mLocal.path.replace(local.path.toFile().getAbsolutePath(), "") + ".aes"));
            }
            found = false;
        }

        for(MetaFileObject mDrive : driveFiles) {
            for(MetaFileObject mLocal : localFiles) {
                if(mDrive.path.replace(".aes", "").equals(mLocal.path)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                toSync.add(new File(mDrive.path), new File(local.path.toFile().getAbsolutePath() + mDrive.path.replace(drive.path.toFile().getAbsolutePath(), "").replace(".aes", "")));
            }
            found = false;
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
            // Folder does not exist
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the path
        FileSystem fs = path.getFileSystem();

        // We create the new WatchService using the new try() block
        try (WatchService service = fs.newWatchService()) {
            
            Kind<?>[] kinds = {ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY};
            
            // We register the path to the service
            // We watch for creation events
            path.register(service, kinds, ExtendedWatchEventModifier.FILE_TREE);

            // Start the infinite polling loop
            WatchKey key = null;
            while (!this.terminated) {
                key = service.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().contains(".metadata.json")) {
                        continue;
                    } else if (event.kind() == ENTRY_DELETE) {
                        Path element = path.resolve(((WatchEvent<Path>)event).context());
                        synchronized (lock) {
                            deleted.add(element);
                            lock.notify();
                        }
                        //System.out.println("Entry deleted: " + element);
                    } else {
                        synchronized (lock) {
                            this.metaData.readOrUpdate();
                            lock.notify();
                        }
                    }
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
     * Updates the JSON file when changes happened
     */
    public void updateJson(){
    	this.metaData.readOrUpdate();
    }

    public void terminate() {
        this.terminated = true;
    }
}
