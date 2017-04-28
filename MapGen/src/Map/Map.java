package Map;

import java.util.Random;

public class Map {
	
	private final int gridSize = 1000;
	
	public Tile[][] map = new Tile[gridSize][gridSize];
	
	
	
	
	//the actual function
	public void generate(long genSeed)
	{
		seed = genSeed;
		midpointDisplacementGen();
		for(int i = 0; i<260; i++)
			blur();
		sharpen();
		//flattenWater();
	}
	
	//graphics options
	int heightSand = 5100;
	int heightForest = 6500;
	int heightGrass = 7500;
	int heightRock = 9500;
	
	
	
	
	private long seed;
	Random rnd = new Random(seed);
	
	public int getAverageSurround(int x, int y, int deltax, int deltay)
	{
		int sum = 0;
		int s = 0;
		
		int nextx = x - deltax;
		int nexty = y;
		try
		{
			sum+=map[nextx][nexty].getHeight();
			s++;
		}catch(Exception e){}
		
		nextx = x+deltax;
		try
		{
			sum+=map[nextx][nexty].getHeight();
			s++;
		}catch(Exception e){}
		
		nextx = x;
		nexty = y - deltay;
		try
		{
			sum+=map[nextx][nexty].getHeight();
			s++;
		}catch(Exception e){}
		
		nexty = y + deltay;
		try
		{
			sum+=map[nextx][nexty].getHeight();
			s++;
		}catch(Exception e){}
		
		return sum/s;

	}
	
	public void doMidPoint(int x1, int y1, int x2, int y2, int noise)
	{
		int x = (x1+x2)/2;
		int y = (y1+y2)/2;

				//corners
		map[x1][y1].setHeight(map[x1][y1].getHeight()+rnd.nextInt(noise*2)-noise);
		map[x2][y2].setHeight(map[x2][y2].getHeight()+rnd.nextInt(noise*2)-noise);
		map[x1][y2].setHeight(map[x1][y2].getHeight()+rnd.nextInt(noise*2)-noise);
		map[x2][y1].setHeight(map[x2][y1].getHeight()+rnd.nextInt(noise*2)-noise);
		
		int midAvg = (map[x1][y1].getHeight()+
				map[x2][y2].getHeight()+
				map[x1][y2].getHeight()+
				map[x2][y1].getHeight())/4;
		
		int smallNoise = noise/2;
		
		if(smallNoise==0)
			smallNoise = 1;
		
		map[x][y].setHeight(midAvg+rnd.nextInt(noise*2)-noise);
		
		
		int deltax = (x2 - x1)/2;
		int deltay = deltax;
		
		int curx = x;
		int cury = y1;
		map[curx][cury].setHeight(getAverageSurround(curx ,cury, deltax, deltay)+rnd.nextInt(smallNoise*2)-smallNoise);
		curx = x1;
		cury = y;
		map[curx][cury].setHeight(getAverageSurround(curx ,cury, deltax, deltay)+rnd.nextInt(smallNoise*2)-smallNoise);
		curx = x;
		cury = y2;
		map[curx][cury].setHeight(getAverageSurround(curx ,cury, deltax, deltay)+rnd.nextInt(smallNoise*2)-smallNoise);
		curx = x1;
		cury = y;
		
		if (Math.abs(x1-x2)>1)
		{
			int nnoise = (int) (noise*reductor);
			if (nnoise<2)
				nnoise = 1;
			//System.out.println(nnoise);
			doMidPoint(x1, y1, x, y, nnoise);
			doMidPoint(x, y1, x2, y, nnoise);
			doMidPoint(x1, y, x, y2, nnoise);
			doMidPoint(x, y, x2, y2, nnoise);
		}
		
	}
	
	public int maxheight = 10000;
	public int waterLevel = maxheight/2;
	public int firstNoise = maxheight/2;
	public double reductor = 0.7;
	
	public void midpointDisplacementGen()
	{
		
		for (int i=0; i < gridSize; i++)
			for (int j=0; j <gridSize; j++)
			{
				map[i][j] = new Tile(maxheight/2);
			}
		
		doMidPoint(0,0,gridSize-1,gridSize-1, firstNoise);
		/*for (int i=0; i < 1000; i++)
			for (int j=0; j <1000; j++)
			{
				//if(map[i][j].getHeight()>1000)
				map[i][j] = new Tile(maxheight/2);
			}*/
	}
	
	public void blur()
	{
		for (int i=0; i < gridSize-1; i++)
			for (int j=0; j <gridSize-1; j++)
			{
				map[i+rnd.nextInt(2)][j+rnd.nextInt(2)].setHeight(
						(map[i][j].getHeight()+
								map[i+1][j].getHeight()+
								map[i][j+1].getHeight()+
								map[i+1][j+1].getHeight()
								)/4);
			}
	}
	
	public void flattenWater()
	{
		for (int i=0; i < gridSize; i++)
			for (int j=0; j <gridSize; j++)
				if(map[i][j].getHeight()<=waterLevel)
					map[i][j].setHeight(waterLevel);
	}
	
	public void sharpen()
	{
		int realmax = 0;
		for (int i=0; i < gridSize; i++)
			for (int j=0; j <gridSize; j++)
				if(map[i][j].getHeight()>realmax)
					realmax = map[i][j].getHeight();
		realmax-=waterLevel;
		
		double coeff = (maxheight-waterLevel)/realmax;
		for (int i=0; i < gridSize; i++)
			for (int j=0; j <gridSize; j++)
			{
				int x = i;
				int y = j;
				if(map[x][y].getHeight()>waterLevel)
				{
					int diff = map[x][y].getHeight() - waterLevel;
					map[x][y].setHeight((int)(waterLevel+diff*coeff));
				}
			}
	}
	
	private double distance(int x1, int y1, int x2, int y2)
    {
        return Math.sqrt( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
    }
	
	/*public void mountainGen(int x, int y)
	{
		Random rand = new Random();
	
		int dx = 50, dy = 50;
		
		try
		{
		for (int i=x-dx; i < x+dx; i++)
		{
			for (int j=y-dy; j <y+dy; j++)
			{	
				int pike = rand.nextInt(10000);
				map[i][j] = new Tile(pike);
			}
			
			//pike--;
		}
		}
		catch (Exception ex)
		{
			
		}
	}*/
	
	
	

}
