package frigatebird.ui;

import java.util.ArrayList;
import java.util.Set;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class TerrainMapVisualizer {
	
	private int tileSizeInPixels;
	
	public int getTileSizeInPixels() {
		return tileSizeInPixels;
	}

	public TerrainMapVisualizer() {
		tileSizeInPixels = 30;
	}
	
	public void drawMap(Canvas editingCanvas, Set<Tile> selectedTileSet, int numColors) {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		TerrainMap map = App.getMap();
		
		double width = editingCanvas.getWidth();
		double length = editingCanvas.getHeight();
		int tempWidthSize = (int) width/map.getNumColumns();
		int tempLengthSize = (int) length/map.getNumRows();
		tileSizeInPixels = Math.min(tempWidthSize, tempLengthSize);
		
		gc.setFill(Color.rgb(245, 245, 245));
		gc.fillRect(0, 0, 1000, 1000);
		
		drawMapTiles(gc, numColors);
		gc.setFill(Color.hsb(300, 1, 1));
		for(Tile tile: selectedTileSet) {
			gc.fillRect(tile.getCol() * tileSizeInPixels, tile.getRow() * tileSizeInPixels, tileSizeInPixels-1, tileSizeInPixels-1);
		}
		drawMapNumbers(gc, selectedTileSet, tileSizeInPixels, numColors);
	}
	
	private void drawMapTiles(GraphicsContext gc, int numColors) {
		TerrainMap map = App.getMap();

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
				double x = c * tileSizeInPixels;
				double y = r * tileSizeInPixels;
				gc.fillRect(x, y, tileSizeInPixels - 1, tileSizeInPixels - 1);
			}
		}
	}
	
	private void drawMapNumbers(GraphicsContext gc, Set<Tile> selectedTileSet, int tileSize, int numColors) {
		TerrainMap map = App.getMap();
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", ((double) tileSize-1)/2);
		gc.setFont(font);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
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
		TerrainMap map = App.getMap();
		
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

	
}
