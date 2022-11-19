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
	private int size;

	public TerrainMap(int rows, int columns) {
		this.tileGrid = new Tile[rows][columns];
		this.numRows = rows;
		this.numColumns = columns;
		this.size = 30;
		
		for(int r = 0; r < numRows; r++) {
			for(int c = 0; c < numColumns; c++) {
				tileGrid[r][c] = new Tile(0, r, c, false);
			}
		}
	}
	
	public TerrainMap() {
		this(15, 15);
	}
	
	public void drawMap(Canvas editingCanvas, Set<Tile> selectedTileSet, int numColors) {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		
		double width = editingCanvas.getWidth();
		double length = editingCanvas.getHeight();
		int tempWidthSize = (int) width/numColumns;
		int tempLengthSize = (int) length/numRows;
		size = Math.min(tempWidthSize, tempLengthSize);
		
		drawMapTiles(gc, numColors);
		gc.setFill(Color.hsb(300, 1, 1));
		for(Tile tile: selectedTileSet) {
			gc.fillRect(tile.getCol() * size, tile.getRow() * size, size-1, size-1);
		}
		drawMapNumbers(gc, selectedTileSet, size, numColors);
	}
	
	private void drawMapTiles(GraphicsContext gc, int numColors) {
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numColumns; c++) {
				Tile tile = getTileAt(r, c);
				int height = tile.getHeight();
				int hue = 230;
				if(tile.getIsPointy()) {
					hue = 0;
				}
				double saturation = 1 - (double) height/numColors;
				double brightness = (double) height/numColors;
				Color color = Color.hsb(hue, saturation, brightness);
				gc.setFill(color);
				double x = c * size;
				double y = r * size;
				gc.fillRect(x, y, size - 1, size - 1);
			}
		}
	}
	
	private void drawMapNumbers(GraphicsContext gc, Set<Tile> selectedTileSet, int tileSize, int numColors) {
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", ((double) tileSize-1)/2);
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
	
	public void drawFrontPerspective(Canvas editingCanvas, int numColors) {
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
				double x = c * size;
				double y = r * size;
				if(height > max) {
					max = height;
				}
			}
			
			heightList.add(max);
			max = 0;
		}
    	int index = heightList.size()-1;
    	// rewrite this loop
		for(int c = 0; c < numColumns; c++) {
			for(int r = index ; r > index - heightList.get(c); r--) {
				double x = c * size;
				double y = r * size;
				double saturation = (double) (r+1)/numColors;
				double brightness = 1 - (double) (r+1)/numColors;
				gc.setFill(Color.hsb(230, saturation, brightness));
				gc.fillRect(x, y, size-1, size-1);
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
	
	public int getTileSize() {
		return size;
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
