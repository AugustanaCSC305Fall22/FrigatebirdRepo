package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EditorController {

	@FXML 
	private Canvas editingCanvas;

	@FXML
	private void initialize() {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		
		TerrainMap map = App.getMap();
		
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				gc.setStroke(Color.AQUA);
				double x = c * 30;
				double y = r * 30;
				gc.strokeRect(x, y, 30, 30);
				
				Tile tile = map.getTileAt(r, c);
				
				// draw filled rectangle where the color
				// is determined by the height of the Tile
			}
		}
	
		
		
	}
	
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
}