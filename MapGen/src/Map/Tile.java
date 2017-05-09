package Map;

import java.io.Serializable;

public class Tile implements Serializable {
	
	private int height;
	private double waterLevel;
	
	public boolean isUpdated;
	
	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
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
