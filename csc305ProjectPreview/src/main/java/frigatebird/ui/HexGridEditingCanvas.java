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
	
	public HexGridEditingCanvas(TerrainMap map, double width, double height, int tileSizeInPixels, int border) {
		super(map, width, height, tileSizeInPixels, border);
		this.radius = getTileSizeInPixels() / 2.0;
		this.n = Math.sqrt(radius * radius * 0.75);
        this.tileHeight = 2 * radius;
        this.tileWidth = 2 * n;
	}
	
	@Override
	public void drawMap(Set<Tile> selectedTileSet, int numColors) {
		GraphicsContext gc = getGraphicsContext2D();
		
		double width = getWidth();
		double length = getHeight();
		int tempWidthSize = (int) width/getMap().getNumColumns();
		int tempLengthSize = (int) length/getMap().getNumRows();
		setTileSizeInPixels(Math.min(tempWidthSize, tempLengthSize));
		radius = getTileSizeInPixels() / 2.0;
		n = Math.sqrt(radius * radius * 0.75);
        tileHeight = 2 * radius;
        tileWidth = 2 * n;
		
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
		        double[] yPoints = {y, y + radius, y + radius * 1.5, y + radius, y, y - radius * 0.5};
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
				gc.fillText(Integer.toString(height), x + tileWidth / 2, y + n * 5 / 8);
			}
		}
	}
	
	@Override
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
					gc.strokeLine(x, y, x + tileWidth, y + y + radius);
					gc.strokeLine(x, y + radius, x + tileWidth, y);
				}
			}
		}
		gc.setLineWidth(1);
	}
	
}
