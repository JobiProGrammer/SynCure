package syncure.core;


import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
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


    public Tree(Path path, Object lock) {
        this.path = path;
        this.lock = lock;
    }

    public ArrayList<File> compare(Tree other) {
        return null;
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
                    while (true) {
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
                    ioe.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }

            }


    /**
     * Updates the jason file when chanes happened
     */
    private void updateJson(){
    	MetaData md = new MetaData(path.resolve("\\.metadata.json"));
    	md.writeinitFiles();
    }

    public static void main(String[] args) throws IOException,
        InterruptedException {
        // Folder we are going to watch
        // Path folder =
        // Paths.get(System.getProperty("C:\\Users\\Isuru\\Downloads"));

        new Thread(new Tree(FileSystems.getDefault().getPath("Test"), new Object())).start();

        }
}
