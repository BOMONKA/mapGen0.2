package Map;

import java.io.Serializable;

public class Tile implements Serializable {
	
	private int height;
	private double waterLevel;
	private int type;
	
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
		this.type = 0;
		this.waterLevel = 0;
	}
	
	public Tile(int height, int type)
	{
		this.height = height;
		this.type = type;
		this.waterLevel = 0;
	}
	
	public Tile(Tile t)
	{
		this.height = t.height;
		this.waterLevel = t.waterLevel;
		this.type = t.type;
	}
	
	/*public Tile(Tile reference)
	{
		//this.height = reference.height;
		//this.waterLevel = reference.waterLevel;
		
	}*/
	
	public int getHeight()
	{
		return height;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int t)
	{
		this.type = t;
	}
	
	
	public void setHeight(int height)
	{
		this.height = height;
	}

}
