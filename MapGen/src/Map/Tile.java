package Map;

public class Tile {
	
	private double height;
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
	
	public double getHeight()
	{
		return height;
	}
	
	public void setHeight(double height)
	{
		this.height = height;
	}

}
