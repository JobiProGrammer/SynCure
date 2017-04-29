package syncure.core;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Created by david on 29.04.17.
 */
public class ToSync {
    public ArrayList<File> source;
    public ArrayList<File> target;

    public void add(File source, File target) {
        this.source.add(source);
        this.target.add(target);
    }
}
