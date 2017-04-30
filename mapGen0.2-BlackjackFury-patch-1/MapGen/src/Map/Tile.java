package Map;

public class Tile {
	
	private double height;
	private double waterLevel;
	private double erased;
	
	
	
	public double getErased() {
		return erased;
	}

	public void setErased(double erased) {
		this.erased = erased;
	}

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
		this.erased = 0;
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
