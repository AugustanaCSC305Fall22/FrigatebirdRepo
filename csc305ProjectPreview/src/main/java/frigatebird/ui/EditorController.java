package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class EditorController {

	@FXML 
	private Canvas editingCanvas;
	private TerrainMap map;

	@FXML
	private void initialize() {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		
		this.map = App.getMap();
		editingCanvas.setOnMousePressed( e -> doMouse(e));

		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				gc.setStroke(Color.BLACK);
				double x = c * 30;
				double y = r * 30;
				gc.strokeRect(x, y, 30, 30);
				
				Tile tile = map.getTileAt(r, c);
				
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
        if((map.getNumColumns() * 30) < x) {
        	return -1;
        }
        double colWidth = editingCanvas.getWidth() / map.getNumColumns();
        int col = (int)( x / colWidth);
        if (col >= map.getNumColumns())
            return map.getNumColumns();
        else
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
        if((map.getNumRows() * 30) < y) {
        	return -1;
        }
        double rowHeight = editingCanvas.getHeight() / map.getNumRows();
        int row = (int)(y / rowHeight);
        if (row >= map.getNumRows())
            return map.getNumRows();
        else
            return row;
    }	
    
    private void doMouse(MouseEvent evt) {
        int row = yCoordToRowNumber((int)evt.getY());
        int col = xCoordToColumnNumber((int)evt.getX());
        if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
               // (the test in this if statement will be false if the user drags the
               //  mouse outside the canvas after pressing the mouse on the canvas)
            System.out.println("You clicked in the box.");
            System.out.println(map.getNumRows());
            System.out.println(map.getNumColumns());
        } else {
        	System.out.println("Not in the box loser");
        }
    }
	
	
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
}