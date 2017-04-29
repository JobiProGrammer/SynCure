package main;

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
	//TODO Implementieren von vielen verschlüsslungsstufen
	//TODO erkennen ob richter schlüssel mit eigener Datei
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SecretKeySpec key;
	//private File selectedFile;
	
	public interface whatToDo{
		public byte[] what(byte[] text);
	}
	
	public Crypto(String keyStr){
		key=Crypto.createKey(keyStr);
	}
	
	public String dEncrypt(String text){
		return new String(encrypt(text.getBytes(), key));
	}
	
	public String dDecrypt(String text){
		return new String(decrypt(text.getBytes(), key));
	}
	
	
	public byte[] dEncrypt1(byte[] text){
		return encrypt(text, key);
	}
	
	public byte[] dDecrypt1(byte[] text){
		return decrypt(text, key);
	}
	
	private static File chooseDir(boolean dir){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		if(dir)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JFrame frame = new JFrame();
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
		    return fileChooser.getSelectedFile();
		    
		}
		return null;
	}
	
	
	
	
	private void cryptFile(File selectedFile, boolean en){
		
		FileInputStream fis;
		try {
			//System.out.println(selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1));
			if(!en && !selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1).equals("aes"))
				return;
			
			fis = new FileInputStream(selectedFile);
			byte[] data = new byte[(int) selectedFile.length()];
			fis.read(data);
			fis.close();
			
			//data=cryptoMeth.what(data);
			if(en)
				data=dEncrypt1(data);
			else
				data=dDecrypt1(data);
			
			
			
			FileOutputStream fos = new FileOutputStream(selectedFile, false);
			fos.write(data);
			fos.close();
			
			if(en)
				selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".aes"));
			else
				selectedFile.renameTo(new File(selectedFile.getAbsolutePath().replace(".aes", "")));
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void cryptDirRec(File folder, boolean en){
		for (File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	cryptDirRec(fileEntry, en);
	        } else {
	            //System.out.println(fileEntry.getName());
	        	cryptFile(fileEntry, en);
	        }
	    }
	}
	
	
	
	public void crypt_Dir_File(boolean dir, boolean en){
		File selectedFile = chooseDir(dir);
		if(selectedFile==null)
			return;
		System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		System.out.println("Sure you want to en/decrypt that with your Key: ");
		//Scanner scanner = new Scanner(System.in);
		String s = readString("Selected file: " + selectedFile.getAbsolutePath() + "\n" + "Sure you want to en/decrypt that with your Key: ");
		if(   !s.equals("y")){//!scanner.nextLine().equals("y")){
			System.out.println("Interrupted");
			return;
		}
		if(!dir)
			cryptFile(selectedFile, en);
		else
			cryptDirRec(selectedFile, en);
		System.out.println("Done");
		
	}
	
	
	
	
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
	
	public static byte[] encrypt(byte[] text, SecretKeySpec secretKeySpec){
	 
	      // Verschluesseln
	      Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		      byte[] encrypted = cipher.doFinal(text);
		   // bytes zu Base64-String konvertieren (dient der Lesbarkeit)
		      //BASE64Encoder myEncoder = new BASE64Encoder();
		      //Base64 b;
		      //String geheim = Base64.getEncoder().encode(encrypted);//  .encodeToString(encrypted);//   myEncoder.encode(encrypted);
		      
		      //return geheim;
		      return Base64.getEncoder().encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	     
	}
	public static byte[] decrypt(byte[] geheim, SecretKeySpec secretKeySpec){
		// BASE64 String zu Byte-Array konvertieren
	      //BASE64Decoder myDecoder2 = new BASE64Decoder();
	      byte[] crypted2 = Base64.getDecoder().decode(geheim);//  myDecoder2.decodeBuffer(geheim);
	 
	      // Entschluesseln
	      
	      try {
	    	  Cipher cipher2 = Cipher.getInstance("AES");
			cipher2.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] cipherData2 = cipher2.doFinal(crypted2);
			return cipherData2;
		      //return new String(cipherData2);
		 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String readString(String text) {
        JFrame frame = new JFrame();
        String s = JOptionPane.showInputDialog(frame, text);
        frame.dispose();

        if (s == null)
            System.exit(0);
        return s;
    }
	
	public static void doJob(){
		
		if(readString("Crypt or Secpass (0/1): ").equals("1")){
			createPassFile(20, 15);
			return;
		}
		
		
		//readString("Enter your Password: ");
		
		String pass = readString("Enter your Password: ");
		Crypto c = new Crypto(pass);
		if(readString("Encode or decode. 1 vor decode else encode").equals("1")){
			//System.out.println("hallo");
			c.crypt_Dir_File(true, false);
		}else{
			c.crypt_Dir_File(true, true);
		}
		
	}
	
	public static int getRandom(int minval, int maxval){
        return minval + (new java.util.Random()).nextInt(maxval-minval+1);
    }
	
	public static String createSecurePasswords(int len, boolean allSpecial, String allowedSpecial){
		String pass = "";
		if(allSpecial){
			for (int i=0; i<len; i++){
				pass+=(char)getRandom(32, 126);
			}
		}
		return pass;
	}
	
	public static void createPassFile(int howMany, int len){
		File f = chooseDir(false);
		try {
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			for (int i=0; i<howMany; i++){
				writer.println(createSecurePasswords(len, true, ""));
			}
			writer.close();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	

	public static void main(String[] args){
		doJob();
		//System.out.println(createSecurePasswords(20, true, ""));
//		Crypto c = new Crypto("hallo");
//		c.crypt_Dir_File(true, true);
//		System.out.println(c.dEncrypt("sadfasdfa"));
//		System.out.println(c.dDecrypt("1nj3mWRQKZ6JpyoSRIkBQg=="));

	}

}
