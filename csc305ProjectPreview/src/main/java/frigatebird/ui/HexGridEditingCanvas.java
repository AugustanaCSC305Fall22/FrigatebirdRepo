package frigatebird.ui;

import java.util.Set;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class HexGridEditingCanvas extends GridEditingCanvas {
	
	private double radius;
    private double n;
    private double tileHeight;
    private double tileWidth;
    private double shift;
	
    /**
     * Creates a canvas with a size and a TerrainMap used to generate a hexagonal grid.
	 * @param map - a TerrainMap object which will be displayed in the canvas
	 * @param width - the width to make the canvas in pixels
	 * @param height - the height to make the canvas in pixels
	 * @param tileSizeInPixels - the side length of a tile in pixels
	 * @param border - the size of the grid lines and border in pixels
     */
	public HexGridEditingCanvas(TerrainMap map, double width, double height, int tileSizeInPixels, int border) {
		super(map, width, height, tileSizeInPixels, border);
		this.n = getTileSizeInPixels() / 2.0;
		this.radius = n * 2 / Math.sqrt(3);
        this.tileHeight = 2 * radius;
        this.tileWidth = 2 * n;
        this.shift = n / Math.sqrt(3);
	}
	
	
	@Override
	
	/**
	 * Draws the the grid editing canvas with all the hexagonal tiles in the map data field
	 * 
	 * @param editingCanvas - a given Canvas object to draw and draw on
	 * @param selectedTileSet - a set of Tiles to draw on the canvas
	 * @param numColors - an int used to generate the color of tiles based on the height of the highest tile
	 */
	public void drawMap(Set<Tile> selectedTileSet, int numColors) {
		GraphicsContext gc = getGraphicsContext2D();
		
		double width = getWidth();
		double length = getHeight();
		double tempWidthSize = width/((double) getMap().getNumColumns() + 0.5);
		double tempLengthSize = length/((double) getMap().getNumRows() * 1.5 / Math.sqrt(3) + 0.5 / Math.sqrt(3));
		setTileSizeInPixels((int) Math.min(tempWidthSize, tempLengthSize));
		n = getTileSizeInPixels() / 2.0;
		radius = n * 2 / Math.sqrt(3);
        tileHeight = 2 * radius;
        tileWidth = 2 * n;
        shift = n / Math.sqrt(3);
		
		gc.setFill(Color.rgb(245, 245, 245));
		gc.fillRect(0, 0, getWidth(), getHeight());
		
		drawMapTiles(gc, selectedTileSet, numColors);
		drawPointyTiles(gc, selectedTileSet);
		drawMapNumbers(gc, selectedTileSet, numColors);
	}
	
	
	
	@Override

	protected void drawMapTiles(GraphicsContext gc, Set<Tile> selectedTileSet, int numColors) {
		for (int r = 0; r < getMap().getNumRows(); r++) {
			for (int c = 0; c < getMap().getNumColumns(); c++) {
				Tile tile = getMap().getTileAt(r, c);
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
		        double x = c * tileWidth;
		        double y = r * tileHeight * 0.75;
				if(r % 2 == 1) {
					x += n;
				}
		        // creates the polygon using the corner coordinates
		        double[] xPoints = {x, x, x + n, x + tileWidth, x + tileWidth, x + n};
		        double[] yPoints = {y + shift, y + radius + shift, y + radius * 1.5 + shift, y + radius + shift, y + shift, y - radius * 0.5 + shift};
				gc.fillPolygon(xPoints, yPoints, 6);
			}
		}
	}
	
	@Override
	protected void drawMapNumbers(GraphicsContext gc, Set<Tile> selectedTileSet, int numColors) {
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", radius);
		gc.setFont(font);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		for (int r = 0; r < getMap().getNumRows(); r++) {
			for (int c = 0; c < getMap().getNumColumns(); c++) {
				Tile tile = getMap().getTileAt(r, c);
				int height = tile.getHeight();
				double x = c * tileWidth;
		        double y = r * tileHeight * 0.75;
				if(r % 2 == 1) {
					x += n;
				}
				if(height > numColors/2 || selectedTileSet.contains(tile)) {
					gc.setFill(Color.BLACK);
				}
				else {
					gc.setFill(Color.WHITE);
				}
				gc.fillText(Integer.toString(height), x + tileWidth / 2, y + n * 5 / 8 + shift);
			}
		}
	}
	
	@Override
	
	
	
	/**
	 * Draws signification of pointy tiles on the canvas
	 * @param gc Graphics context used to draw pointyness indication
	 * @selectedTileSet set of tiles that have been selected. 
	 */
	protected void drawPointyTiles(GraphicsContext gc, Set<Tile> selectedTileSet) {
		Color color = Color.rgb(255, 100, 100);
		gc.setStroke(color);
		gc.setLineWidth(3);
		for (int r = 0; r < getMap().getNumRows(); r++) {
			for (int c = 0; c < getMap().getNumColumns(); c++) {
				Tile tile = getMap().getTileAt(r, c);
				if(tile.getIsPointy()) {
					double x = c * tileWidth;
			        double y = r * tileHeight * 0.75;
					if(r % 2 == 1) {
						x += n;
					}
					gc.strokeLine(x, y + shift, x + tileWidth, y + radius + shift);
					gc.strokeLine(x, y + radius + shift, x + tileWidth, y + shift);
				}
			}
		}
		gc.setLineWidth(1);
	}
	
}
