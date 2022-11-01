package frigatebird.terrainbuilder;

public class TerrainMap {
	
	private Tile[][] tileGrid;
	private int numRows;
	private int numColumns;
	
	public TerrainMap(int rows, int columns) {
		this.tileGrid = new Tile[rows][columns];
		this.numRows = rows;
		this.numColumns = columns;
		
		for(int r = 0; r < numRows; r++) {
			for(int c = 0; c < numColumns; c++) {
				tileGrid[r][c] = new Tile();
			}
		}
	}
	
	public TerrainMap() {
		this(5, 3);
	}
	
	public Tile getTileAt(int row, int col) {
		return tileGrid[row][col];
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}


}
