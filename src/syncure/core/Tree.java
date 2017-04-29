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

                        // Dequeueing events
                        Kind<?> kind = null;
                        for (WatchEvent<?> watchEvent : key.pollEvents()) {
                            // Get the type of the event
                            kind = watchEvent.kind();
                            if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind || ENTRY_DELETE == kind) {
                                // A new Path was created
                                Path newPath = ((WatchEvent<Path>) watchEvent).context();
                                lock.notify();
                            }
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





    public static void main(String[] args) throws IOException, InterruptedException {


        new Thread(new Tree(FileSystems.getDefault().getPath("Test"), new Object())).start();

        }
}
