package syncure.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * MeteData Klasse handelt die Jason Metadatei und sorgt zwischen der kommunikation vom verzeichnisbaum
 * @author Tobias
 *
 */
public class MetaData {
	//public HashMap<String, String> data;
	
	private File path;
	private File metaFile;
	
	private ArrayList<MetaFileObject> FileIndexList = new ArrayList<MetaFileObject>();
	
	/**
	 * Prüft ob das verzeichnis noch nie syncronisiert wurde
	 * @param path
	 * @return
	 */
	public static boolean isNew(Path path){
		try {
			FileInputStream fis = new FileInputStream(path.toFile());
			return false;
		}catch(Exception e){
			return true;
		}
	}
	
	/**
	 * Erstellt das Objekt
	 * @param path
	 */
	public MetaData(Path path) {
		this.path= path.toFile();
		this.metaFile = new File(path.toFile().getAbsolutePath() + "\\.metadata.json");
		
	}
	

	
	/**
	 * 
	 * @return Liste aller Dateien in Verzeichnis und allen Unterverzeichnissen
	 */
	public ArrayList<MetaFileObject> getData(){
		
		FileInputStream fis;
		String alldata="";
		try {
			fis = new FileInputStream(metaFile);
		
			//list alles aus der Datei
			byte[] data = new byte[(int) metaFile.length()];
			fis.read(data);
			fis.close();
			alldata = new String(data);
		} catch (Exception e1) {
			e1.printStackTrace();
			
			//ertsellt die Datei falls sie nicht existiert
			try {
				metaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Gson gson = new Gson();
		try{
			MetaFileObject[] mto = gson.fromJson(alldata, MetaFileObject[].class);
			if(mto==null)
				throw new Exception();
			FileIndexList = new ArrayList<MetaFileObject>(Arrays.asList(mto));
			
		}catch (Exception e){
			initFiles();
			setData();
		}
		
		return FileIndexList;
	}
	
	/**
	 * Schreibe json in die metadata datei
	 * @param FileIndexList
	 */
	public void setData(ArrayList<MetaFileObject> FileIndexList){
		Gson gson = new Gson();
		String data = gson.toJson(FileIndexList.toArray());
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(metaFile, false);
			fos.write(data.getBytes());
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * setzte data
	 */
	public void setData(){
		setData(FileIndexList);
	}
	
	/**
	 * Schreibe json vom aktuellen dateisystem
	 */
	public void writeinitFiles(){
		initFiles();
		setData();
	}
	

	/**
	 * lese dateien aus
	 */
	public void initFiles(){
		FileIndexList = new ArrayList<MetaFileObject>();
		recFolder(path);
	}

	private void recFolder(File source){
		for (File fileEntry : source.listFiles()) {
            if (fileEntry.isDirectory()) {
            	recFolder(fileEntry);
            //verhindert das die metadata selber dabei ist
            } else if(!source.getAbsolutePath().contains(".metadata.json")){
            	System.out.println(source.getAbsolutePath());
            	addList(fileEntry, fileEntry.lastModified());
            }
        }
	}

	
	private void addList(File source, long time){
		FileIndexList.add(new MetaFileObject(source.getAbsolutePath(), time));
	}
	
	public ArrayList<MetaFileObject> getFileIndexList(){
		return FileIndexList;
	}
	
//	public static void main(String[] args) {
//		MetaData md = new MetaData(Paths.get("C:\\Users\\Tobias\\Documents\\GitHub"));
//		md.getData();
//		md.setData();
//	}
	

}