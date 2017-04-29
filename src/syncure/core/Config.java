package syncure.core;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

import static java.nio.file.FileSystem.*;


public class Config {

    private String password;
    private Path localDirectory;
    private Path driveDirectory;

    private static File configFile = new File(getUserDataDirectory()+"config.json");


    private Crypto crypto;
    
    
    /**
     * 
     * @author Tobias
     * Config data um alles als ein objekt zu übergeben
     */
    private class ConfigData{
		public String password;
		public String localDirectory;
		public String driveDirectory;
		public ConfigData(String password, String localDirectory, String driveDirectory){
			this.password=password;
			this.localDirectory=localDirectory;
			this.driveDirectory=driveDirectory;
		}
	};

	/**
	 * 
	 * @param configFilePath kann null sein, dann wird /home/.syncure/config.txt genommen
	 */
    public Config(Path configFilePath) {
		if (configFilePath != null)
			configFile = configFilePath.toFile();
		readConfig();
	}
    
    /**
     * schreibt die aktuellen Daten (zb wenn verändert) in die Datei
     */
    public void setConfig(){
		Gson gson = new Gson();
		String data = gson.toJson(getInfo());
		
		try {
			FileOutputStream fos = new FileOutputStream(configFile, false);
			fos.write(data.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * liest die COnfig von der Datei ein
     */
    public void readConfig(){
    	FileInputStream fis;
		String alldata="";
		try {
			fis = new FileInputStream(configFile);
		
			//list alles aus der Datei
			byte[] data = new byte[(int) configFile.length()];
			fis.read(data);
			fis.close();
			alldata = new String(data);
		} catch (Exception e1) {
			e1.printStackTrace();
			
			//ertsellt die Datei falls sie nicht existiert
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Gson gson = new Gson();
		try{
			ConfigData cd = gson.fromJson(alldata, ConfigData.class);
			if(cd==null)
				throw new Exception();
			readInto(cd);
			
		}catch (Exception e){
			
			ConfigData cd = new ConfigData("", "", "");
			readInto(cd);
			setConfig();
		}
		System.out.println("Password: "+password);
		System.out.println("Sync: "+localDirectory);
		System.out.println("-> "+driveDirectory);
    }
    
    

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocalDirectory(Path localDirectory) {
        this.localDirectory = localDirectory;
    }

    public void setDriveDirectory(Path driveDirectory) {
        this.driveDirectory = driveDirectory;
    }



    public Path getLocalDirectory() {
        return localDirectory;
    }

    public Path getDriveDirectory() {
        return driveDirectory;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
    
    /**
     * 
     * @param cd setzte das configdata objekt in das confik objekt
     */
    private void readInto(ConfigData cd){
    	password = cd.password;
    	localDirectory = Paths.get(cd.localDirectory);
    	driveDirectory = Paths.get(cd.driveDirectory);
    	crypto = new Crypto(password);
    }
    
    /**
     * 
     * @return erstelle confik objekt
     */
    private ConfigData getInfo(){
    	return new ConfigData(password, localDirectory.toAbsolutePath().toString(), driveDirectory.toAbsolutePath().toString());
    }
    
    /**
     * 
     * @return Verzeichnis für evtuelle Configdateien etc
     */
  	private static String getUserDataDirectory() {
  		//erstellen des Verzeichnisses
  	    String file= System.getProperty("user.home") + File.separator + ".syncure"  + File.separator;
  	    File f = new File(file);
  	    //Verändert nichts falls das Verzeichnis existiert
  	    f.mkdir();
  	    return file;
  	}
//  	public static void main(String[] args) {
//  		Config c=new Config(null);
//  		c.setPassword("bla");
//  		c.setConfig();
//  	}

}


