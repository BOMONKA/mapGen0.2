package Map;

public class Tile {
	
	private int height;
	private double waterLevel;
	
	
	
	public double getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(double waterLevel) {
		this.waterLevel = waterLevel;
	}

	public Tile(int height)
	{
		this.height = height;
		this.waterLevel = 0;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}

}
