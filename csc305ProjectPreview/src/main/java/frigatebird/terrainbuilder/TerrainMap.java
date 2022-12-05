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

public class TerrainMap implements Cloneable{
	
	private Tile[][] tileGrid;
	private int numRows;
	private int numColumns;
	private Set<Tile> selectedTileSet;

	public TerrainMap(int rows, int columns) {
		this.tileGrid = new Tile[rows][columns];
		this.numRows = rows;
		this.numColumns = columns;
		this.selectedTileSet  = new HashSet<Tile>();
		
		for(int r = 0; r < numRows; r++) {
			for(int c = 0; c < numColumns; c++) {
				tileGrid[r][c] = new Tile(0, r, c, false);
			}
		}
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
	
	public Set<Tile> getSelectedTileSet() {
		return selectedTileSet;
	}
	
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
		/*
		TerrainMap clone = new TerrainMap(this.numRows, this.numColumns);
		clone.tileGrid = new Tile[this.numRows][this.numColumns];
		for(int r = 0; r < numRows; r++) {
			for(int c = 0; c < numColumns; c++) {
				clone.tileGrid[r][c] = getTileAt(r, c).clone();
			}
		}
		return clone;
		*/
	}
	
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
