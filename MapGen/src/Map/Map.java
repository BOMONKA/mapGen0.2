package Map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Map implements Runnable {
	
	public int gridSize = 128;
	
	public int getGridSize() {

		return gridSize;
	}

	//private Tile[][] buffer = new Tile[gridSize + 1][gridSize + 1];
	//public ArrayList<WaterSource> wc = new ArrayList<WaterSource>();
	//public ArrayList<Tile> updTiles = new ArrayList<Tile>();
	
	public int maxheight = 10000; //in 1cm!!! (etc. 100m - maxheight while generating)
	public int oceanlevel = 2000;
	public int maxworldheight = 160000;
	public int firstNoise = maxheight/8;
	private int height = maxheight/2;
	public double reductor = 0.7;
	private long startTime;
	private double ldist;
	private Random rand = new Random(); 
	private ArrayList<Point> mountains = new ArrayList<Point>();
	public WorldMap wm = new WorldMap();
	public boolean addheights = false; //true does not work
	
	
	//the actual function
	
	public void generate(long genSeed, WorldMap worldmap, int startX, int startY, int size)
	{	
		gridSize = size-1;
		wm = worldmap;
		ldist = distance(0, 0, gridSize/2, gridSize/2);
		rand = new Random(genSeed);
		zeroSizdes(startX, startY);
		if(wm.getTile(startX+gridSize/2, startY+gridSize/2) == null)
			wm.setTile(startX+gridSize/2, startY+gridSize/2, new Tile(maxheight));
		midpointDisplacementGen(startX,startY,gridSize + startX,gridSize + startY, maxheight);
	}
	
	public void mountainGen(int x, int y, int x1, int y1, int offset)
	{
		if( y1 - y > 10)
		{
			int halfX = (x+x1)/2;
			int halfY = (y+y1)/2;
			int newX = halfX + rand.nextInt(offset) - offset/2;
			mountains.add(new Point(newX, halfY));
			offset= offset/2;
			if(offset < 1)
				offset = 1;
			Point v = new Point();
			mountainGen(x,y,newX,halfY, offset);
			mountainGen(newX,halfY,x1,y1, offset);
		}
	}
	
	public void zeroSizdes(int x, int y)
	{
		for (int i = 0; i < gridSize+1; i++)
		{
			if(wm.getTile(i+x,y) == null)
				wm.setTile(i + x, y, new Tile(0)); //map[i][0] = new Tile(0);
			if(wm.getTile(x,y+i) == null)
				wm.setTile(x, i+y, new Tile(0));
			if(wm.getTile(i+x,gridSize+y) == null)
				wm.setTile(i+x, gridSize+y, new Tile(0));
			if(wm.getTile(gridSize+x,y+i) == null)
				wm.setTile(gridSize+x, i+y, new Tile(0));
			//System.out.println("set height");
			
			/*buffer[0][i] = new Tile(0);
			buffer[i][0] = new Tile(0);
			buffer[gridSize][i] = new Tile(0);
			buffer[i][gridSize] = new Tile(0);*/
		}
	}
	
	public void square(int x, int y, int x1, int y1, int noise)
	{
		int halfX = (x + x1)/2;
		int halfY = (y + y1)/2;
		int mHeight = (wm.getTile(x,y).getHeight() + wm.getTile(x1,y).getHeight() + wm.getTile(x,y1).getHeight() + wm.getTile(x1,y1).getHeight())/4;
		//int mHeight = (buffer[x][y].getHeight() + buffer[x1][y].getHeight() + buffer[x][y1].getHeight() + buffer[x1][y1].getHeight())/4;
		mHeight += rand.nextInt(noise) - (int)((noise/2)) ;
		if(mHeight<0)
			mHeight = 0;
		
		if (wm.getTile(halfX, halfY) == null)
		{
			wm.setTile(halfX, halfY, new Tile(mHeight));
		}
		else if(addheights)
		{
			wm.setTile(x, y, new Tile(mHeight + wm.getTile(x,y).getHeight()));
		}
	}
	
	
	public void diamond(int x, int y, int offset, int noise)
	{
		int success = 0;
		
		int mHeight = 0;    
		try
		{
			//mHeight += map[x-offset][y].getHeight();
			mHeight += wm.getTile(x - offset, y).getHeight();
			//mHeight += buffer[x - offset][y].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			//mHeight +=  map[x][y-offset].getHeight();
			mHeight += wm.getTile(x, y - offset).getHeight();
			//mHeight += buffer[x][y - offset].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			//mHeight += map[x][y + offset].getHeight();
			mHeight += wm.getTile(x, y+offset).getHeight();
			//mHeight += buffer[x][y + offset].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		try
		{
			//mHeight += map[x + offset][y].getHeight();
			mHeight += wm.getTile(x + offset, y).getHeight();
			//mHeight += buffer[x + offset][y].getHeight();
			success ++;
		} 
		catch(Exception e)
		{
			
		}
		
		
		mHeight = mHeight/success;
		mHeight += rand.nextInt(noise) - (int)((noise/2));
		
		if(mHeight<0)
			mHeight = 0;
		
		if (wm.getTile(x,y) == null)
		{
			//System.out.println(mHeight);
			wm.setTile(x, y, new Tile(mHeight));
			//buffer[x][y] = new Tile(mHeight);
		}
		else if(addheights)
		{
			wm.setTile(x, y, new Tile(mHeight + wm.getTile(x,y).getHeight()));
		}
	}
	
	public void sleep(int time)
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void midpointDisplacementGen(int x, int y, int x1, int y1, int noise)
	{
		
		//sleep(1);
		
		noise = (int) (noise * reductor);
		if (noise <  1)
			noise = 1;
		if ((x1-x) > 1 && (y1-y) > 1)
		{
			
			square(x,y,x1, y1, noise);
			
			int halfX = (x + x1)/2;
			int halfY = (y + y1)/2;
			int offset = halfX - x;
			
			
			diamond(x, halfY, offset, noise);
			diamond(halfX, y, offset, noise);
			diamond(x1, halfY, offset, noise);
			diamond(halfX, y1, offset, noise);
			
			midpointDisplacementGen(x,y,halfX,halfY, noise);
			midpointDisplacementGen(halfX,y,x1,halfY, noise);
			midpointDisplacementGen(halfX,halfY,x1,y1, noise);
			midpointDisplacementGen(x,halfY,halfX,y1, noise);
			
		}
	}
	
	public void start()
	{
		new Thread(this).start();
	}
	@Override
	public void run() {
		
		//generate(0);
		System.out.println("done");
	//	blur(10);
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
	private static double distance(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    }
	
}
