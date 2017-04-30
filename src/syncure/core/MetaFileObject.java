package syncure.core;

/**
 * 
 * @author Tobias
 *	Wraps MetaFiles Data
 */
public class MetaFileObject {
	public String path;
	public long time;
	public MetaFileObject(String path, long time){
		this.path=path;
		this.time=time;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof MetaFileObject) {
			MetaFileObject metafile = (MetaFileObject) object;
			return path.equals(metafile.path) && time == metafile.time;
		}
		return false;
	}
}
