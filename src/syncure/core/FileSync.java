package syncure.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
                copyDir(source, target, encrypt, config);
            } else {
                copyFile(source, target, encrypt, config);
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
