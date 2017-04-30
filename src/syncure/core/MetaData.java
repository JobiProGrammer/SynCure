package syncure.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;


/**
 * MetaData Klasse handlet die JSON-Metadatei und sorgt zwischen der Kommunikation vom Verzeichnisbaum
 * @author Tobias
 *
 */
public class MetaData {
	//public HashMap<String, String> data;
	
	private File path;
	private File metaFile;
	
	private ArrayList<MetaFileObject> fileIndexList = new ArrayList<MetaFileObject>();
	
	/**
	 * Prüft, ob das Verzeichnis noch nie synchronisiert wurde
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
        this.fileIndexList = new ArrayList<MetaFileObject>();

        readOrUpdate();
	}
	
	/**
	 * 
	 * @return Liste aller Dateien im Verzeichnis und allen Unterverzeichnissen
	 */
	public ArrayList<MetaFileObject> getData(){
		
		FileInputStream fis;
		String alldata="";
		try {
			fis = new FileInputStream(metaFile);
		
			//liest alles aus der Datei
			byte[] data = new byte[(int) metaFile.length()];
			fis.read(data);
			fis.close();
			alldata = new String(data);
		} catch (Exception e1) {
			//erstellt die Datei, falls sie nicht existiert
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
			fileIndexList = new ArrayList<MetaFileObject>(Arrays.asList(mto));
			
		}catch (Exception e){
			initFiles();
			setData();
		}
		
		return fileIndexList;
	}
	
	/**
	 * Schreibe JSON in die metadata Datei
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
	 * setze data
	 */
	public void setData(){
		setData(fileIndexList);
	}
	
    public ArrayList<MetaFileObject> readOrUpdate() {
	    ArrayList<MetaFileObject> tempList = new ArrayList<>();
	    recFolder(tempList, path);

	    boolean equal = true;
	    for (MetaFileObject meta : tempList) {
	        if (!fileIndexList.contains(meta)) {
	            equal = false;
	            break;
            }
        }

        if (!equal) {
	        initFiles();
	        setData();
        }
        return this.fileIndexList;
    }

	/**
	 * lese Dateien aus
	 */
	public void initFiles(){
		recFolder(fileIndexList, path);
	}

	private void recFolder(ArrayList<MetaFileObject> list, File source){
		for (File fileEntry : source.listFiles()) {
            if (fileEntry.isDirectory()) {
            	recFolder(list, fileEntry);
            //verhindert, dass die metadata selber dabei ist
            } else if(!source.getAbsolutePath().contains(".metadata.json")){
            	//System.out.println(source.getAbsolutePath());
            	addList(list, fileEntry, fileEntry.lastModified());
            }
        }
	}

	
	private void addList(ArrayList<MetaFileObject> list, File source, long time){
		if(!source.getAbsolutePath().substring(source.getAbsolutePath().lastIndexOf(".") + 1).equals("json"))
            list.add(new MetaFileObject(source.getAbsolutePath(), time));
	}
	
	public ArrayList<MetaFileObject> getFileIndexList(){
		return fileIndexList;
	}
	
//	public static void main(String[] args) {
//		MetaData md = new MetaData(Paths.get("C:\\Users\\Tobias\\Documents\\GitHub"));
//		md.getData();
//		md.setData();
//	}
	

}