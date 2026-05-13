package assets;

public abstract class Asset {
	
	public String filePath;
	
	public int id;
	
	public abstract void release();
	
	public int getRawID() {
		return (this.id << 8) >> 8;
	}

}
