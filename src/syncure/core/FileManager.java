package syncure.core;

import java.io.File;
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
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {

            }
            //ArrayList<File> sources = local.compare(remote);
            //ArrayList<File> targets = remote.compare(local);
            sync(null, null, true);
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
