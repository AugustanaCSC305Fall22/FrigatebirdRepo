package frigatebird.terrainbuilder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class TerrainMapTest {

	@Test
	void testCreateSquareTerrainMap() {
		TerrainMap map = new TerrainMap("Test", 10, 12, false);
		assertEquals(10, map.getNumRows());
		assertEquals(12, map.getNumColumns());
	}
	
	
	@Test
	void testNonHexGetCenterX() {
		TerrainMap map = new TerrainMap("Test", 5, 5, false);
		int tileWidth = 5;
		int assertValueIncrement = 0;
		for(int r = 0; r < map.getNumRows(); r++) {
			for(int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c); 
				assertEquals(2.5 + assertValueIncrement, tile.getCenterX(tileWidth));
			}
			assertValueIncrement += 5;
		}
	}
	
	@Test
	void testHexGetCenterX() {
		TerrainMap map = new TerrainMap("Test", 5, 5, true);
		int tileWidth = 5;
		int assertValueIncrement = 0;
		for(int r = 0; r < map.getNumRows(); r++) {
			for(int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c); 
				if(r % 2 == 0) {
					assertEquals(2.5 + assertValueIncrement, tile.getCenterX(tileWidth));
				}else {
					assertEquals(5 + assertValueIncrement, tile.getCenterX(tileWidth));
				}
			}
			assertValueIncrement += 5;
		}
	}
	
	

	

}
