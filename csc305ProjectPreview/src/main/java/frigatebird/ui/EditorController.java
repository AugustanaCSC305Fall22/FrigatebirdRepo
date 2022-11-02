package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class EditorController {
	
	@FXML 
	private Canvas editingCanvas;
	private TerrainMap map;
	private int tileSize = 30;
	private int numColors = 16;

	@FXML
	private void initialize() {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		
		this.map = App.getMap();
		editingCanvas.setOnMousePressed(e -> changeHeight(e, 1));
		gc.setStroke(Color.BLACK);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				int colorHeight = height*(256/numColors);
				if (colorHeight > 255) {
					colorHeight = 255;
				}
				Color color = Color.rgb(colorHeight, colorHeight, 255);
				gc.setFill(color);
				double x = c * tileSize;
				double y = r * tileSize;
				gc.fillRect(x, y, tileSize - 1, tileSize - 1);
				gc.setFill(Color.BLACK);
				if(tile.getHeight() < 10) {
					gc.fillText(Integer.toString(height), x + 10, y + 20);
				} else {
					gc.fillText(Integer.toString(height), x + 7, y + 20);
				}
				// draw filled rectangle where the color
				// is determined by the height of the Tile
				}
			}
		}

	/**
     * Given an x-coordinate of a pixel in the MosaicCanvas, this method returns
     * the row number of the mosaic rectangle that contains that pixel.  If
     * the x-coordinate does not lie within the bounds of the mosaic, the return
     * value is -1 or is equal to the number of columns, depending on whether
     * x is to the left or to the right of the mosaic.
     */
    public int xCoordToColumnNumber(double x) {
        if (x < 0)
            return -1;
        if(x >= (map.getNumColumns() * tileSize)) {
        	return -1;
        }
        int col = (int)(x / tileSize);
        return col;
    }

    /**
     * Given a y-coordinate of a pixel in the MosaicCanvas, this method returns
     * the column number of the mosaic rectangle that contains that pixel.  If
     * the y-coordinate does not lie within the bounds of the mosaic, the return
     * value is -1  or is equal to the number of rows, depending on whether
     * y is above or below the mosaic.
     */
    public int yCoordToRowNumber(double y) {
        if (y < 0)
            return -1;
        if(y >= (map.getNumRows() * tileSize)) {
        	return -1;
        }
        int row = (int)(y / tileSize);
        return row;
    }	
    
    private void changeHeight(MouseEvent evt, int num) {
    	int row = yCoordToRowNumber((int)evt.getY());
        int col = xCoordToColumnNumber((int)evt.getX());
    	Tile tile = map.getTileAt(row, col);
	    if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
	        if (evt.getButton().equals(MouseButton.PRIMARY) && tile.getHeight() < 15) {
	        	tile.setHeight(tile.getHeight() + num);
	        } else if (evt.getButton().equals(MouseButton.SECONDARY) && tile.getHeight() > 0) {
	        	tile.setHeight(tile.getHeight() - num);
	        }
	           try {
				refresh();
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
    }
	
	@FXML
    private void refresh() throws IOException {
        App.setRoot("EditPage");
    }
	
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
}