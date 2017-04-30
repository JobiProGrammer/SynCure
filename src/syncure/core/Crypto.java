package syncure.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Crypto{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Key repräsentiert durch das SecretKeySpec Objekt
	private SecretKeySpec key;
	
	/**
	 * Konstruktor
	 * @param keyStr Schlüssel als String
	 */
	public Crypto(String keyStr){
		key=Crypto.createKey(keyStr);
	}
	
	/**
	 * dEncrypt und dDecrypt zum ver und entschlüssel von string oder byte daten mit key
	 * @param text der zu verschlüsselne text
	 * @return verschlüsselter string
	 */
	public String dEncrypt(String text){
		return new String(encrypt(text.getBytes(), key));
	}
	
	public String dDecrypt(String text){
		return new String(decrypt(text.getBytes(), key));
	}
	
	
	public byte[] dEncrypt(byte[] text){
		return encrypt(text, key);
	}
	
	public byte[] dDecrypt(byte[] text){
		return decrypt(text, key);
	}
	
	
	/**
	 * öffnet einen File browser um eine Datei auszuwählen
	 * @param dir Ordner oder Datei auswählen
	 * @return ausgewählte datei und 
	 */
	public static File chooseDir(boolean dir, File file){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(file);
		if(dir)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JFrame frame = new JFrame();
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
		    return fileChooser.getSelectedFile();
		    
		}
		return null;
	}
	public static File chooseDir(boolean dir){
		return chooseDir(dir, new File(System.getProperty("user.home")));
	}
	
	
	
	/**
	 * verchlüsselt eine Datei, en -> encrypt en=false -> decrypt
	 * @param selectedFile
	 * @param en verschlüsseln oder entschlü+sseln
	 */
	private void cryptFile(File selectedFile, boolean en){
		
		FileInputStream fis;
		try {
			if(!en && !selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1).equals("aes"))
				return;
			
			fis = new FileInputStream(selectedFile);
			byte[] data = new byte[(int) selectedFile.length()];
			fis.read(data);
			fis.close();
			
			if(en)
				data=dEncrypt(data);
			else
				data=dDecrypt(data);
			
			
			
			FileOutputStream fos = new FileOutputStream(selectedFile, false);
			fos.write(data);
			fos.close();
			
			if(en)
				selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".aes"));
			else
				selectedFile.renameTo(new File(selectedFile.getAbsolutePath().replace(".aes", "")));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * get den einen Ordnerstrucktur rekursiv durch und ver oder entschlüsselt das Objekt
	 * @param folder
	 * @param en
	 */
	private void cryptDirRec(File folder, boolean en){
		for (File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	cryptDirRec(fileEntry, en);
	        } else {
	        	cryptFile(fileEntry, en);
	        }
	    }
	}
	
	
	/**
	 * fragt den Nutzer welchen Ordner/Datei er versclüsseln möchte und verschlüsselt
	 * diese mittels cryptDirRec rekursiv
	 * @param dir
	 * @param en
	 */
	public void crypt_Dir_File(boolean dir, boolean en){
		File selectedFile = chooseDir(dir);
		if(selectedFile==null)
			return;
		System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		System.out.println("Sure you want to en/decrypt that with your Key: ");
		String s = readString("Selected file: " + selectedFile.getAbsolutePath() + "\n" + "Sure you want to en/decrypt that with your Key: ");
		if(   !s.equals("y")){
			System.out.println("Interrupted");
			return;
		}
		if(!dir)
			cryptFile(selectedFile, en);
		else
			cryptDirRec(selectedFile, en);
		System.out.println("Done");
		
	}
	
	
	
	/**
	 * Generiert den Key mittles eines Strings
	 * @param keyStr
	 * @return
	 */
	public static SecretKeySpec createKey(String keyStr){      
		try {
			byte[] key;
			key = (keyStr).getBytes("UTF-8");
			 // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
		      MessageDigest sha = MessageDigest.getInstance("SHA-256");
		      key = sha.digest(key);
		   // nur die ersten 128 bit nutzen
		      key = Arrays.copyOf(key, 16); 
		      // der fertige Schluessel
		      return new SecretKeySpec(key, "AES");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}     
	}
	
	/**
	 * verschlüsselt den byte text mittels einem übergeben key (ist statisch)
	 * @param text
	 * @param secretKeySpec
	 * @return
	 */
	public static byte[] encrypt(byte[] text, SecretKeySpec secretKeySpec){
	 
	      // Verschluesseln
	      Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		      byte[] encrypted = cipher.doFinal(text);
		      // bytes zu Base64-String konvertieren (dient der Lesbarkeit)
		      return Base64.getEncoder().encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	     
	}
	
	/**
	 * verschlüsselt den byte text mittels einem übergeben key (ist statisch)
	 * @param geheim
	 * @param secretKeySpec
	 * @return
	 */
	public static byte[] decrypt(byte[] geheim, SecretKeySpec secretKeySpec){
		// BASE64 String zu Byte-Array konvertieren
	      byte[] crypted2 = Base64.getDecoder().decode(geheim);
	 
	      // Entschluesseln
	      
	      try {
	    	  Cipher cipher2 = Cipher.getInstance("AES");
			cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] cipherData2 = cipher2.doFinal(crypted2);
			return cipherData2;
		 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * liest einen String von der
	 * @param text
	 * @return
	 */
	public static String readString(String text) {
        JFrame frame = new JFrame();
        String s = JOptionPane.showInputDialog(frame, text);
        frame.dispose();

        if (s == null)
            System.exit(0);
        return s;
    }
	

	/**
	 * Generiert im interval eine zufällige zahl
	 * @param minval
	 * @param maxval
	 * @return
	 */
	public static int getRandom(int minval, int maxval){
        return minval + (new java.util.Random()).nextInt(maxval-minval+1);
    }
	
	/**
	 * Genreriert ein zufaääliges passwort
	 * @param len
	 * @param allSpecial
	 * @param allowedSpecial
	 * @return
	 */
	public static String createSecurePasswords(int len, boolean allSpecial, String allowedSpecial){
		String pass = "";
		if(allSpecial){
			for (int i=0; i<len; i++){
				pass+=(char)getRandom(32, 126);
			}
		}
		return pass;
	}
	
	


}
