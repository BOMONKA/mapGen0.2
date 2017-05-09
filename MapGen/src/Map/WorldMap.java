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
	
	private String saveTo = "map\\";
	public int chunkSize = 1000;
	public int worldWidth = 200;
	public int worldHeight = 100;
	private ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();
	private int chunkSaveTime = 60000;
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
		
		return new Vec2(glob.x%worldWidth, glob.y%worldHeight);
	}
	
	private Vec2 getChunkLocal(Vec2 glob)
	{
		return new Vec2(glob.x - glob.x%worldWidth, glob.y - glob.y%worldHeight);
	}

	
	private Chunk isLoadedWithCoordinates(Vec2 coords)
	{
		for(Chunk c : loadedChunks)
		{
			if(c.x == coords.x && c.y == coords.y)
			{
				return c;
			}
		}
		return null;
	}
	
	
	public void setTile(int x, int y, Tile tile)
	{
		Vec2 chunkCoords = getTileChunk(new Vec2(x, y));
		Chunk required = isLoadedWithCoordinates(new Vec2(x, y) );
		Vec2 localCoords = getChunkLocal(new Vec2(x ,y));
		if(required != null)
		{
			required.lastRequested = System.currentTimeMillis();
			required.data[localCoords.x][localCoords.y] = tile;
		}
		else
		{
			required = readChunk(chunkCoords);
			required.lastRequested = System.currentTimeMillis();
			required.data[x][y] = tile;
		}
	}
	
	public Tile getTile(int x, int y)
	{	
		Vec2 chunkCoords = getTileChunk(new Vec2(x, y));
		Chunk required = isLoadedWithCoordinates(new Vec2(x, y) );
		Vec2 localCoords = getChunkLocal(new Vec2(x ,y));
		if(required != null)
		{
			required.lastRequested = System.currentTimeMillis();
			return required.data[localCoords.x][localCoords.y];
		}
		else
		{
			required = readChunk(chunkCoords);
			required.lastRequested = System.currentTimeMillis();
			return required.data[x][y];
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
		System.out.println("saved");
		readChunk(new Vec2(0,0));
		System.out.println("loaded");
		return c;
	}
	
	private Chunk readChunk(Vec2 cVec)
	{
		FileInputStream fis;
		Chunk c = new Chunk(chunkSize);
		try {
			fis = new FileInputStream(saveTo+cVec.x+"-"+cVec.y+".chunk");
			ObjectInputStream oin = new ObjectInputStream(fis);
			c = (Chunk) oin.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.x = cVec.x;
		c.y = cVec.y;
		c.lastRequested = System.currentTimeMillis();
		loadedChunks.add(c);
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
			  oos.writeObject(c);
			  oos.flush();
			  oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
