package Map;

import java.util.ArrayList;
import java.util.Random;

public class Map implements Runnable {
	
	private final int gridSize = 1024;
	
	public int getGridSize() {
		return gridSize;
	}

	public Tile[][] map = new Tile[gridSize + 1][gridSize + 1];
	public ArrayList<WaterSource> wc = new ArrayList<WaterSource>();
	public ArrayList<Tile> updTiles = new ArrayList<Tile>();
	public int maxheight = 255;
	public int waterLevel = 50000;
	public int firstNoise = maxheight/8;
	private int height = maxheight/2;
	public double reductor = 0.1;
	private long startTime;
	private Random rand = new Random();
	//the actual function
	
	public void generate(long genSeed)
	{
		Random r = new Random();
		map[0][0] = new Tile(r.nextInt(maxheight));
		map[gridSize][0] = new Tile(r.nextInt(maxheight));
		map[0][gridSize] = new Tile(r.nextInt(maxheight));
		map[gridSize][gridSize] = new Tile(r.nextInt(maxheight));
		midpointDisplacementGen(0,0,gridSize,gridSize);
		
	}
	
	public void square(int x, int y, int x1, int y1)
	{
		int halfX = (x + x1)/2;
		int halfY = (y + y1)/2;
	
		
		int mHeight = (map[x][y].getHeight() +  map[x1][y].getHeight() +  map[x][y1].getHeight()+  map[x1][y1].getHeight())/4;
		
		map[halfX][halfY] = new Tile(mHeight + rand.nextInt(height) - height/2);
		
		
		
		
		
	}
	
	public void diamond(int x, int y, int offset)
	{
		
		
		int success = 0;
		
		int mHeight = 0;    
		try
		{
			mHeight += map[x-offset][y].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			mHeight +=  map[x][y-offset].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			mHeight += map[x][y + offset].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			mHeight += map[x + offset][y].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		mHeight = mHeight/success;
		
		map[x][y] = new Tile(mHeight + rand.nextInt(height) - height/2);
	}

	public void midpointDisplacementGen(int x, int y, int x1, int y1)
	{
		
		if ((x1-x) > 1 && (y1-y) > 1)
		{
			square(x,y,x1, y1);
			
			int halfX = (x + x1)/2;
			int halfY = (y + y1)/2;
			int offset = halfX - x;
			
			
			diamond(x, halfY, offset);
			diamond(halfX, y, offset);
			diamond(x1, halfY, offset);
			diamond(halfX, y1, offset);
			
			midpointDisplacementGen(x,y,halfX,halfY);
			midpointDisplacementGen(halfX,y,x1,halfY);
			midpointDisplacementGen(halfX,halfY,x1,y1);
			midpointDisplacementGen(x,halfY,halfX,y1);
		}
	}

	
	public void start()
	{
		
	}
	@Override
	public void run() {
		while (true)
		{
			startTime = System.currentTimeMillis();
		//		updateWater();
			long deltaTime = System.currentTimeMillis() - startTime;
		//	System.out.println(deltaTime);
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
	
}
