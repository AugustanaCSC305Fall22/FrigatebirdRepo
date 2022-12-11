package frigatebird.terrainbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Makes a grid map out of Tile objects in a 2D array
 */
public class TerrainMap implements Cloneable {
	
	private Tile[][] tileGrid;
	private String mapName;
	private int numRows;
	private int numColumns;
	private boolean hexagonal;
	
	/**
	 * Makes a grid map out of Tile objects in a 2D array
	 * 
	 * @param mapName - name of the TerrainMap for purposes of saving
	 * @param rows - number of rows of tiles in this TerrainMap object and it's 2D array
	 * @param columns - number of columns of tiles in this TerrainMap object and it's 2D array
	 */
	public TerrainMap(String mapName, int rows, int columns, boolean hexagonal) {
		this.tileGrid = new Tile[rows][columns];
		this.mapName = mapName;
		this.numRows = rows;
		this.numColumns = columns;
		this.hexagonal = true;
		
		for(int r = 0; r < numRows; r++) {
			for(int c = 0; c < numColumns; c++) {
				tileGrid[r][c] = new Tile(0, r, c, false, hexagonal);
			}
		}
	}
	
	
	/**
	 * Returns the Tile object stored at a row and column location in the TerrainMap
	 * 
	 * @param row - the row the Tile to find at is located in
	 * @param col - the column the Tile to find at is located in
	 * @return - the Tile object located at this row and column combination in this TerrainMap object
	 */
	public Tile getTileAt(int row, int col) {
		return tileGrid[row][col];
	}
	
	/**
	 * Returns the name of the TerrainMap object
	 * 
	 * @return - Returns the string name of the TerrainMap object
	 */
	public String getName() {
		return mapName;
	}

	/**
	 * Returns the number of rows of the TerrainMap object
	 * 
	 * @return - Returns the int number of rows of the TerrainMap object
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * Returns the number of columns of the TerrainMap object
	 * 
	 * @return - Returns the int number of columns of the TerrainMap object
	 */
	public int getNumColumns() {
		return numColumns;
	}
	
	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public boolean isHexagonal() {
		return hexagonal;
	}

	public void setHexagonal(boolean hexagonal) {
		this.hexagonal = hexagonal;
	}

	/**
	 * Returns highest height of all the tiles in the TerrainMap object
	 * 
	 * @return - Returns highest height of all the tiles in the TerrainMap object
	 */
	public int findMaxMapHeight() {
		int max = 0;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numColumns; c++) {
				Tile tile = getTileAt(r, c);
				if(max < tile.getHeight()) {
					max = tile.getHeight();
				}
			}
		}
		return max;
	}
	
	/**
	 * Creates a copy of this TerrainMap and returns it
	 * 
	 * @return - an exact copy of the TerrainMap object
	 */
	public TerrainMap clone() {
		try {
			TerrainMap clone = (TerrainMap) super.clone();
			clone.tileGrid = new Tile[this.numRows][this.numColumns];
			for(int r = 0; r < numRows; r++) {
				for(int c = 0; c < numColumns; c++) {
					clone.tileGrid[r][c] = getTileAt(r, c).clone();
				}
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			// should never happen
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Overrides the toString() method to return a string with info on the map name, and number of rows and columns in the map
	 * 
	 * @return - a string with info on the map name, and number of rows and columns in the map
	 */
	@Override
	public String toString() {
		String text = "TerrainMap [tileGrid=[";
		for(int r = 0; r < numRows; r++) {
			text += "[";
			for(int c = 0; c < numColumns; c++) {
				text += tileGrid[r][c].toString();
			}
			text += "]";
		}
		text += "], numRows=" + numRows + ", numColumns=" + numColumns + "]";
		return text;
	}
}
