package syncure.core;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileManager implements Runnable{

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
        Tree localTree = new Tree(config.getLocalDirectory(), lock);
        Tree remoteTree = new Tree(config.getDriveDirectory(), lock);
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
            //ArrayList<File> sources = localTreeWatcher.compare(remoteTreeWatcher);
            //ArrayList<File> targets = remoteTreeWatcher.compare(localTreeWatcher);
            sync(null, null, true);
            
        }
    }

    public void terminateThreads() {
        localTreeWatcher.interrupt();
        remoteTreeWatcher.interrupt();
    }

    public void terminate() {
        this.terminated = true;

        try {
            terminateThreads();
        } catch (Exception e) {

        }
    }

    public void sync(ArrayList<File> sources, ArrayList<File> targets, boolean syncAll) {
    	if(syncAll){
    		FileSync.copyDir(config.getLocalDirectory().toFile(), config.getDriveDirectory().toFile(), true, config);
    		return;
    	}
    		
    	if(sources.size()!=targets.size()){
    		return;
    	}
    	int len = sources.size();
    	for(int i = 0; i<len; i++){
    		if(isTarget(targets.get(i))){
    			FileSync.copyFile(sources.get(i), targets.get(i), true, config);
    		}else{
    			FileSync.copyFile(sources.get(i), targets.get(i), false, config);
    		}
    	}
    }
    
    private boolean isTarget(File f){
    	return f.getAbsolutePath().contains(config.getDriveDirectory().toAbsolutePath().toString());
    }
}
