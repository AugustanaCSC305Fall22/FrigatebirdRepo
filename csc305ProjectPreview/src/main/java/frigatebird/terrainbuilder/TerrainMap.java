package frigatebird.terrainbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
				tileGrid[r][c] = new Tile(0, r, c);
			}
		}
	}
	
	public TerrainMap() {
		this(15, 15);
	}
	
	public void drawMap(Canvas editingCanvas, Set<Tile> selectedTileSet, int tileSize, int numColors) {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		Color color = Color.rgb(245, 245, 245);
		gc.setFill(color);
		gc.fillRect(0, 0, 1000, 1000);
		drawMapTiles(gc, tileSize, numColors);
		gc.setFill(Color.hsb(300, 1, 1));
		for(Tile tile: selectedTileSet) {
			gc.fillRect(tile.getCol() * tileSize, tile.getRow() * tileSize, tileSize-1, tileSize-1);
		}
		drawMapNumbers(gc, selectedTileSet, tileSize, numColors);
	}
	
	private void drawMapTiles(GraphicsContext gc, int tileSize, int numColors) {
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numColumns; c++) {
				Tile tile = getTileAt(r, c);
				int height = tile.getHeight();
				double saturation = 1 - (double) height/numColors;
				double brightness = (double) height/numColors;
				Color color = Color.hsb(230, saturation, brightness);
				gc.setFill(color);
				double x = c * tileSize;
				double y = r * tileSize;
				gc.fillRect(x, y, tileSize - 1, tileSize - 1);
			}
		}
	}
	
	private void drawMapNumbers(GraphicsContext gc, Set<Tile> selectedTileSet, int tileSize, int numColors) {
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", 14);
		gc.setFont(font);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numColumns; c++) {
				Tile tile = getTileAt(r, c);
				int height = tile.getHeight();
				double x = c * tileSize;
				double y = r * tileSize;
				if(height > numColors/2 || selectedTileSet.contains(tile)) {
					gc.setFill(Color.BLACK);
				}
				else {
					gc.setFill(Color.WHITE);
				}
				if (tile.getHeight() < 10) {
					gc.fillText(Integer.toString(height), x + tileSize / 2, y + tileSize / 2);
				} else {
					gc.fillText(Integer.toString(height), x + tileSize / 2, y + tileSize / 2);
				}
			}
		}
	}
	
	public void drawFrontPerspective(Canvas editingCanvas, int tileSize) {
    	GraphicsContext gc = editingCanvas.getGraphicsContext2D();
    	ArrayList<Integer> heightList = new ArrayList<>();
		int height = 0;
		int max = Integer.MIN_VALUE;
		Color color = Color.rgb(245, 245, 245);
		gc.setFill(color);
		gc.fillRect(0, 0, 1000, 1000);
		for(int c = 0; c < numColumns; c++) {
			for(int r = 0; r < numRows; r++) {
				height = getTileAt(r, c).getHeight();
				double x = c * tileSize;
				double y = r * tileSize;

		    	//getTileAt(r, c);
		    	gc.setFill(Color.WHITE);
				gc.fillRect(x, y, tileSize-1, tileSize-1);
				
				if(height > max) {
					max = height;
				}
			}
			
			heightList.add(max);
			max = 0;
		}
    	int index = heightList.size()-1;
  
		for(int c = 0; c < numColumns; c++) {
			for(int r = index ; r > index - heightList.get(c); r--) {
				double x = c * tileSize;
				double y = r * tileSize;
		    	
		    	//getTileAt(r, c);
				gc.setFill(Color.BLACK);
				gc.fillRect(x, y, tileSize-1, tileSize-1);
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
