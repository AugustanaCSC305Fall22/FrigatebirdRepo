package frigatebird.terrainbuilder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import frigatebird.ui.*;
import frigatebird.terrainbuilder.*;

class TerrainMapTest {

	@Test
	void testCreateSquareTerrainMap() {
		TerrainMap map = new TerrainMap("Test", 10, 12, false);
		assertEquals(10, map.getNumRows());
		assertEquals(12, map.getNumColumns());
	}

}
