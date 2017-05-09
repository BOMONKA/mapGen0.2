package Map;

public class MapGenerator {
	
	
	
	Tile[][] create(int side)
	{
		Tile[][] map = new Tile[side][side];
		generate(map, side);
		return map;
	}
	
	private void generate(Tile[][] map, int size)
	{
		
	}
}
