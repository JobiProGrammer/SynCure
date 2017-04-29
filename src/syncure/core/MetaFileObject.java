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
}
