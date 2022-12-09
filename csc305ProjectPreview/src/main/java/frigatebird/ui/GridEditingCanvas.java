package frigatebird.ui;

import java.util.ArrayList;
import java.util.Set;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * A subclass of canvas which uses a grid to display a TerrainMap
 * 
 * State pattern and memento methods marked as authored by Dale Skrien are from examples given by Skrien as part of his book, "Object Oriented Design Using Java".
 */
public class GridEditingCanvas extends Canvas {

	private int tileSizeInPixels;
	private int border;
	private TerrainMap map;

	/**
	 * Creates a canvas with a size and a TerrainMap used to generate a grid.
	 * 
	 * @param map - a TerrainMap object which will be displayed in the canvas
	 * @param width - the width to make the canvas in pixels
	 * @param height - the height to make the canvas in pixels
	 * @param tileSizeInPixels - the side length of a tile in pixels
	 * @param border - the size of the grid lines and border in pixels
	 */
	public GridEditingCanvas(TerrainMap map, double width, double height, int tileSizeInPixels, int border) {
		super(width, height);
		
		this.map = map;
		this.tileSizeInPixels = tileSizeInPixels;
		this.border = border;
		tileSizeInPixels = 10;
	}
	
	/**
	 * Sets the map variable in the GridEditingCanvas map data field
	 * 
	 * @param map - a TerrainMap object to set the GridEditingCanvas map data field to
	 */
	public void setMap(TerrainMap map) {
		this.map = map;
	}
	
	/**
	 * Returns the TerrainMap map data field of the GridEditingCanvas class
	 * 
	 * @return - the TerrainMap map data field of this GridEditingCanvas
	 */
	public TerrainMap getMap() {
		return map;
	}

	/**
	 * Returns the side length of the tiles in this GridEditingCanvas via the tileSizeInPixels data field
	 * 
	 * @return - the tileSizeInPixels data field
	 */
	public int getTileSizeInPixels() {
		return tileSizeInPixels;
	}
	
	public void setTileSizeInPixels(int tileSizeInPixels) {
		this.tileSizeInPixels = tileSizeInPixels;
	}
	
	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
	}

	/**
	 * Draws the GridEditingCanvas with all the Tiles in the map data field
	 * 
	 * @param editingCanvas - a given Canvas object to draw and draw on
	 * @param selectedTileSet - a set of Tiles to draw on the canvas
	 * @param numColors - an int used to generate the color of tiles based on the height of the highest tile
	 */
	public void drawMap(Set<Tile> selectedTileSet, int numColors) {
		GraphicsContext gc = getGraphicsContext2D();
		
		double width = getWidth();
		double length = getHeight();
		int tempWidthSize = (int) width/map.getNumColumns();
		int tempLengthSize = (int) length/map.getNumRows();
		tileSizeInPixels = Math.min(tempWidthSize, tempLengthSize);
		
		gc.setFill(Color.rgb(245, 245, 245));
		gc.fillRect(0, 0, getWidth(), getHeight());
		
		drawMapTiles(gc, selectedTileSet, numColors);
		drawPointyTiles(gc, selectedTileSet);
		drawMapNumbers(gc, selectedTileSet, numColors);
	}
	
	protected void drawMapTiles(GraphicsContext gc, Set<Tile> selectedTileSet, int numColors) {

		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				int hue = 230;
				if(tile.getIsPointy()) {
					hue = 0;
				}
				double saturation = 1 - (double) height/numColors;
				double brightness = (double) height/numColors;
				Color color = Color.hsb(hue, saturation, brightness);
				gc.setFill(color);
				if(selectedTileSet.contains(tile)) {
					gc.setFill(Color.LIGHTBLUE);
				}
				double x = c * tileSizeInPixels;
				double y = r * tileSizeInPixels;
				gc.fillRect(x, y, tileSizeInPixels - border, tileSizeInPixels - border);
			}
		}
	}
	
	protected void drawPointyTiles(GraphicsContext gc, Set<Tile> selectedTileSet) {
		Color color = Color.rgb(255, 100, 100);
		gc.setStroke(color);
		gc.setLineWidth(3);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				if(tile.getIsPointy()) {
					double x = c * tileSizeInPixels;
					double y = r * tileSizeInPixels;
					gc.strokeLine(x, y, x + tileSizeInPixels - border, y + tileSizeInPixels - border);
					gc.strokeLine(x, y + tileSizeInPixels - border, x + tileSizeInPixels - border, y);
				}
			}
		}
		gc.setLineWidth(1);
	}
	
	protected void drawMapNumbers(GraphicsContext gc, Set<Tile> selectedTileSet, int numColors) {
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", ((double) tileSizeInPixels-1)/2);
		gc.setFont(font);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				double x = c * tileSizeInPixels;
				double y = r * tileSizeInPixels;
				if(height > numColors/2 || selectedTileSet.contains(tile)) {
					gc.setFill(Color.BLACK);
				}
				else {
					gc.setFill(Color.WHITE);
				}
				gc.fillText(Integer.toString(height), x + tileSizeInPixels / 2, y + tileSizeInPixels / 2);
			}
		}
	}
	
	/**
	 * Draws the TerrainMap on the canvas from a side view perspective
	 * 
	 * @param editingCanvas - a canvas to draw the side view on
	 * @param numColors - an int used to determine which colors to use on the Tiles based on the height of the highest tile
	 */
	public void drawFrontPerspective(Canvas editingCanvas, int numColors) {
    	GraphicsContext gc = editingCanvas.getGraphicsContext2D();
    	ArrayList<Integer> heightList = new ArrayList<>();
		
		int height = 0;
		int max = Integer.MIN_VALUE;
		Color color = Color.rgb(245, 245, 245);
		gc.setFill(color); 
		gc.fillRect(0, 0, 1000, 1000);
		for(int c = 0; c < map.getNumColumns(); c++) {
			for(int r = 0; r < map.getNumRows(); r++) {
				height = map.getTileAt(r, c).getHeight();
				double x = c * tileSizeInPixels;
				double y = r * tileSizeInPixels;
				if(height > max) {
					max = height;
				}
			}
			
			heightList.add(max);
			max = 0;
		}
    	int index = heightList.size()-1;
    	// rewrite this loop
		for(int c = 0; c < map.getNumColumns(); c++) {
			for(int r = index ; r > index - heightList.get(c); r--) {
				double x = c * tileSizeInPixels;
				double y = r * tileSizeInPixels;
				double saturation = (double) (r+1)/numColors;
				double brightness = 1 - (double) (r+1)/numColors;
				gc.setFill(Color.hsb(230, saturation, brightness));
				gc.fillRect(x, y, tileSizeInPixels-1, tileSizeInPixels-1);
			}
		}
    }
	
	
	/**
	 * @author Dale Skrien
	 * Creates a memento State object which holds a cloned  copy of a TerrainMap
	 * 
	 * @return - a new instance of the State class which holds a cloned copy of a TerrainMap
	 */
	public State createMemento() {
		return new State();
	}

	/**
	 * @author Dale Skrien
	 * Uses a memento with a cloned copy of a previous TerrainMap to make it the current map
	 * 
	 * @param canvasState - the memento State object containing a cloned copy of a previous TerrainMap
	 */
	public void restoreState(State canvasState) {
		canvasState.restore();
		App.setMap(this.map);
	} 

	/**
	 * @author Dale Skrien
	 * Creates a copy of a TerrainMap to mark its state for future use
	 */
	public class State {
		private TerrainMap tempMap;

		/**
		 * Creates a copy of the current TerrainMap to mark its state
		 */
		public State() {
			tempMap = (TerrainMap) GridEditingCanvas.this.map.clone();
		}

		/**
		 * Restores a previous TerrainMap stored in a state object to the current map
		 */
		public void restore() {
			GridEditingCanvas.this.map = (TerrainMap) tempMap.clone();
		}
	}
}
