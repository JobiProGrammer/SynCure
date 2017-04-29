package syncure.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

/**
 * Created by mikonse on 29.04.2017.
 */
public class FileSync {

    /**
     * Same as copyFile, just for a whole directory
     * @param source source File
     * @param target target File
     * @param encrypt true for encrypt, false for decrypt
     * @param config global Configuration object
     */
    public static void copyDir(File source, File target, boolean encrypt, Config config) {
        for (File fileEntry : source.listFiles()) {
            if (fileEntry.isDirectory()) {
                copyDir(fileEntry, Paths.get(target.getAbsolutePath(), fileEntry.getName()).toFile(), encrypt, config);
            } else {
            	if(!source.getAbsolutePath().contains(".metadata.json")){
            		if (encrypt) {
                        copyFile(fileEntry, Paths.get(target.getAbsolutePath(), fileEntry.getName() + ".aes").toFile(), encrypt, config);
                    } else { // cut .aes from target
                        copyFile(fileEntry, new File(target.getAbsolutePath().replace(".aes", "")), encrypt, config);
                    }
            	}
            }
        }
    }

    /**
     * Copies a file to a target destination and either enrypts it (encrypt = true)
     * or decrypts it (encrypt = false)
     * @param source source File
     * @param target target File
     * @param encrypt true for encrypt, false for decrypt
     * @param config global Configuration object
     */
    public static void copyFile(File source, File target, boolean encrypt, Config config) {
        FileInputStream fis;
        if(!source.getAbsolutePath().contains(".metadata.json")){
        	return;
        }
        try {
            //System.out.println(selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf(".") + 1));
            if (!encrypt && !source.getAbsolutePath().substring(source.getAbsolutePath().lastIndexOf(".") + 1).equals("aes"))
                return;

            // open input stream and read all data as byte array
            fis = new FileInputStream(source);
            byte[] data = new byte[(int) source.length()];
            fis.read(data);
            fis.close();

            // encrypt or decrypt
            if (encrypt)
                data = config.getCrypto().dEncrypt(data);
            else
                data = config.getCrypto().dDecrypt(data);

            // write to output file
            FileOutputStream fos = new FileOutputStream(target, false);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
