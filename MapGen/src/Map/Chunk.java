

package Map;
import java.io.Serializable;


class  Chunk implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public boolean isActive = false;
	public long lastRequested = 0;
	public Tile[][] data;
	
	public Chunk(int chunkSize)
	{
		this.data = new Tile[chunkSize][chunkSize];
	}

}