package syncure.core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MetaData {
	public HashMap<String, String> data;
	
	public MetaData(Path path) {
		Collection<Path> all = new ArrayList<Path>();
		try {
			addTree(path, all);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Path ds : all)
			try {
				data.put(ds.toString(), Files.getLastModifiedTime(ds).toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			gson.toJson(data);
	}
	
	static void addTree(Path directory, Collection<Path> all)
	        throws IOException {
	    try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
	        for (Path child : ds) {
	            all.add(child);
	            if (Files.isDirectory(child)) {
	                addTree(child, all);
	            }
	        }
	    }
	}
}