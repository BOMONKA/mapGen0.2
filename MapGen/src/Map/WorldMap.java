package Map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class WorldMap {

	private class Vec2
	{
		public int x;
		public int y;
		Vec2(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private String saveTo = "C:\\map\\";
	public int chunkSize = 100;
	public int worldWidth = 30;
	public int worldHeight = 30;
	private ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();
	private int chunkSaveTime = 1000;
	private int maxloadedChunks = 1000;
	
	public int getWidth()
	{
		return chunkSize * worldWidth;
	}
	public int getHeight()
	{
		return chunkSize * worldHeight;
	}
	
	private Vec2 getTileChunk(Vec2 glob)
	{
		
		return new Vec2(glob.x/chunkSize, glob.y/chunkSize);
	}
	
	private Vec2 getChunkLocal(Vec2 glob)
	{
		Vec2 tileCoords = getTileChunk(glob);
		return new Vec2(glob.x - tileCoords.x*chunkSize, glob.y - tileCoords.y*chunkSize);
	}

	
	private Chunk isLoadedWithCoordinates(Vec2 coords)
	{
		for(Chunk c : loadedChunks)
		{
			if(c.x == coords.x && c.y == coords.y)
			{
				//System.out.println("Chunk "+coords.x+"-"+coords.y+" is already loaded");
				return c;
			}
		}
		//System.out.println("Loading chunk "+coords.x+"-"+coords.y);
		return null;
	}
	
	
	public void setTile(int x, int y, Tile tile)
	{
		Vec2 v = new Vec2(x, y);
		Vec2 chunkCoords = getTileChunk(v);
		Chunk required = isLoadedWithCoordinates(chunkCoords);
		Vec2 localCoords = getChunkLocal(v);
		//System.out.println("Coords "+x+"-"+y+" are in chunk "+chunkCoords.x+"-"+chunkCoords.y);
		if(required != null)
		{
			required.lastRequested = System.currentTimeMillis();
			required.data[localCoords.x][localCoords.y] = tile;
		}
		else
		{
			required = readChunk(chunkCoords);
			required.lastRequested = System.currentTimeMillis();
			required.data[localCoords.x][localCoords.y] = tile;
		}
	}
	
	public Tile getTile(int x, int y)
	{	
		//System.out.println("Loaded chunks: "+loadedChunks.size());
		Vec2 v = new Vec2(x, y);
		Vec2 chunkCoords = getTileChunk(v);
		Chunk required = isLoadedWithCoordinates(chunkCoords);
		Vec2 localCoords = getChunkLocal(v);
		if(required != null)
		{
			required.lastRequested = System.currentTimeMillis();
			//System.out.println(required.data[localCoords.x][localCoords.y].getHeight());
			return required.data[localCoords.x][localCoords.y];
			
		}
		else
		{
			required = readChunk(chunkCoords);
			required.lastRequested = System.currentTimeMillis();
			//System.out.println(required.data[localCoords.x][localCoords.y].getHeight());
			return required.data[localCoords.x][localCoords.y];
		}
		
	}
	
	
	public int clearChunkmemory()
	{
		int sum = removeOverTime();
		sum += lowerToloadedLimit();
		return sum;
	}
	
	private int lowerToloadedLimit()
	{
		int res = 0;
		while(loadedChunks.size() > maxloadedChunks)
		{
			removeLastunused();
			res++;
		}
		return res;
	}
	
	private void removeLastunused() 
	{
		Chunk toDelete = null;
		long longestDelay = -1;
		for(Chunk c : loadedChunks)
		{
			if(longestDelay == -1)
				longestDelay = System.currentTimeMillis() - c.lastRequested;
			if(System.currentTimeMillis() - c.lastRequested > longestDelay)
			{
				toDelete = c;
				longestDelay = System.currentTimeMillis() - c.lastRequested;
			}
		}
		loadedChunks.remove(toDelete);
	}
	private int removeOverTime() 
	{
		int res = 0;
		for(Chunk c : loadedChunks)
		{
			
			if(!c.isActive && (System.currentTimeMillis() - c.lastRequested > chunkSaveTime))
			{
				loadedChunks.remove(c);
				res++;
			}
		}
		return res;
	}
	
	public Chunk test()
	{
		Chunk c = new Chunk(chunkSize);
		c.x = 0;
		c.y = 0;
		saveChunk(c);
		//System.out.println("saved");
		readChunk(new Vec2(0,0));
		//System.out.println("loaded");
		return c;
	}
	
	public int getLoadedChunks()
	{
		return loadedChunks.size();
	}
	
	public void saveMap()
	{
		int saveAmount = 0;
		System.out.println("loaded chunks "+loadedChunks.size());
		for(Chunk c : loadedChunks)
		{
			saveChunk(c);
			saveAmount++;
		}
		System.out.println(saveAmount);
	}
	
	private Chunk readChunk(Vec2 cVec)
	{
		FileInputStream fis;
		Chunk c = new Chunk(chunkSize);
		try {
			fis = new FileInputStream(saveTo+cVec.x+"-"+cVec.y+".chunk");
			ObjectInputStream oin = new ObjectInputStream(fis);
			c = (Chunk) oin.readObject();
			//System.out.println("read chunk "+cVec.x + "-" +cVec.y+" Chunk[0][0] = "+c.data[0][0].getHeight());
		} catch (Exception e) {
			//e.printStackTrace();
		}
		c.x = cVec.x;
		c.y = cVec.y;
		c.lastRequested = System.currentTimeMillis();
		loadedChunks.add(c);
		//System.out.println("Loaded chunks: "+loadedChunks.size());
		//System.out.println("Loaded Chunk "+cVec.x+"-"+cVec.y);
		return c;
	}
	
	private void saveChunk(Chunk c)
	{
		c.lastRequested = System.currentTimeMillis();
		Vec2 cVec = new Vec2(c.x, c.y);
		String f = saveTo+String.valueOf(cVec.x)+"-"+String.valueOf(cVec.y)+".chunk";
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			System.out.println(c.data[0][0].getHeight());
			  oos.writeObject(c);
			  oos.flush();
			  oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
