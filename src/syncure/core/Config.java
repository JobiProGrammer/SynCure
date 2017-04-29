package syncure.core;

import java.io.IOException;
import java.io.File;
import java.nio.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.FileSystem.*;


public class Config {

    private String password;
    private Path localDirectory;
    private Path driveDirectory;



    private Crypto crypto;

    public Config(Path configFilePath) {
        List<String> file = null;
        try {
            file = Files.readAllLines(configFilePath, Charset.defaultCharset());


        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file == null){
            return;
        }
        if (file.size() != 3){
            System.err.printf("File 'config' represented by %s does not contain exactly 3 entries!", configFilePath.toString());
        }
        password = file.get(0);
        try {
            localDirectory = FileSystems.getDefault().getPath(file.get(1));
            driveDirectory = FileSystems.getDefault().getPath(file.get(2));
        }catch (Exception e){
            e.printStackTrace();
        }
        crypto = new Crypto(password);
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

}


