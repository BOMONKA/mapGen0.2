package Map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class Map implements Runnable {
	
	public final int gridSize = 2048;
	
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
	public double reductor = 0.5;
	private long startTime;
	private double ldist;
	private Random rand = new Random(); 
	private ArrayList<Point> mountains = new ArrayList<Point>();
	
	
	//the actual function
	
	public void generate(long genSeed)
	{
		ldist = distance(0, 0, gridSize/2, gridSize/2);
		rand = new Random(genSeed);
		
		map[0][0] = new Tile(rand.nextInt(maxheight));
		map[gridSize][0] = new Tile(rand.nextInt(maxheight));
		map[0][gridSize] = new Tile(rand.nextInt(maxheight));
		map[gridSize][gridSize] = new Tile(rand.nextInt(maxheight));
		
	//	setHeight();
		int x = rand.nextInt(gridSize);
		int x1 = rand.nextInt(gridSize);
		System.out.println(x);
		System.out.println(x1);
		
		mountainGen(x, 0, x1, gridSize, 1300);
		
	//	map[gridSize/2][gridSize/2] = new Tile(255);
		midpointDisplacementGen(0,0,gridSize,gridSize, maxheight);
	//	blur(100);
		
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
	
	public void setHeight()
	{
		
		map[0][0] = new Tile(0);
		map[gridSize][0] = new Tile(0);
		map[0][gridSize] = new Tile(0);
		map[gridSize][gridSize] = new Tile(0);
		for (int i = 0; i < gridSize; i++)
		{
			map[i][0] = new Tile(0);
		}
		for (int i = 0; i < gridSize; i++)
		{
			map[0][i] = new Tile(0);
		}
		for (int i = 0; i < gridSize; i++)
		{
			map[gridSize][i] = new Tile(0);
		}
		for (int i = 0; i < gridSize; i++)
		{
			map[i][gridSize] = new Tile(0);
		}
	}
	
	public void square(int x, int y, int x1, int y1, int noise)
	{
		int halfX = (x + x1)/2;
		int halfY = (y + y1)/2;

		
		int mHeight = (map[x][y].getHeight() +  map[x1][y].getHeight() +  map[x][y1].getHeight()+  map[x1][y1].getHeight())/4;
		
		
		if (map[halfX][halfY] == null)
		{
			map[halfX][halfY] = new Tile(mHeight + rand.nextInt(noise) - (int)((noise/2)) );	
		}
	}
	
	
	public void diamond(int x, int y, int offset, int noise)
	{
		
		
		//System.out.println(dmod);
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
		if (map[x][y] == null)
		{
			map[x][y] = new Tile(mHeight + rand.nextInt(noise) - (int)((noise/2)));
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
	public void blur(int amount)
	{
		int gridSize = this.gridSize + 1;
		Random rnd = new Random();
		for(int a = 0; a<amount; a++)
		{
			for (int i=0; i < gridSize; i++)
				for (int j=0; j <gridSize; j++)
				{
					map[(i+rnd.nextInt(2))%gridSize][(j+rnd.nextInt(2))%gridSize].setHeight(
							(map[i][j].getHeight()+
									map[(i+1)%gridSize][j].getHeight()+
									map[i][(j+1)%gridSize].getHeight()+
									map[(i+1)%gridSize][(j+1)%gridSize].getHeight()
									)/4);
				}
		}
	}
	
	
	public void start()
	{
		new Thread(this).start();
	}
	@Override
	public void run() {
		
		generate(0);
		System.out.println("done");
		blur(10);
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
