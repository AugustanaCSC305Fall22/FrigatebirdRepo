package frigatebird.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.TerrainMapIO;
import frigatebird.terrainbuilder.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditorController {

	@FXML
	private Canvas editingCanvas;
	private TerrainMap map;
	private int tileSize = 30;
	private int numColors = 16;

	@FXML
	private void initialize() {
		this.map = App.getMap();
		editingCanvas.setOnMousePressed(e -> changeHeight(e, 1));
		drawMap(this.map);
	}

	private void drawMap(TerrainMap map) {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				int colorHeight = height * (256 / numColors);
				if (colorHeight > 255) {
					colorHeight = 255;
				}
				Color color = Color.rgb(colorHeight, colorHeight, 255);
				gc.setFill(color);
				double x = c * tileSize;
				double y = r * tileSize;
				gc.fillRect(x, y, tileSize - 1, tileSize - 1);
				gc.setFill(Color.BLACK);
				if (tile.getHeight() < 10) {
					gc.fillText(Integer.toString(height), x + 10, y + 20);
				} else {
					gc.fillText(Integer.toString(height), x + 7, y + 20);
				}
			}
		}
	}

	/**
	 * Given an x-coordinate of a pixel in the MosaicCanvas, this method returns the
	 * row number of the mosaic rectangle that contains that pixel. If the
	 * x-coordinate does not lie within the bounds of the mosaic, the return value
	 * is -1 or is equal to the number of columns, depending on whether x is to the
	 * left or to the right of the mosaic.
	 */
	public int xCoordToColumnNumber(double x) {
		if (x < 0)
			return -1;
		if (x >= (map.getNumColumns() * tileSize)) {
			return -1;
		}
		int col = (int) (x / tileSize);
		return col;
	}

	/**
	 * Given a y-coordinate of a pixel in the MosaicCanvas, this method returns the
	 * column number of the mosaic rectangle that contains that pixel. If the
	 * y-coordinate does not lie within the bounds of the mosaic, the return value
	 * is -1 or is equal to the number of rows, depending on whether y is above or
	 * below the mosaic.
	 */
	public int yCoordToRowNumber(double y) {
		if (y < 0)
			return -1;
		if (y >= (map.getNumRows() * tileSize)) {
			return -1;
		}
		int row = (int) (y / tileSize);
		return row;
	}

	private void changeHeight(MouseEvent evt, int num) {
		int row = yCoordToRowNumber((int) evt.getY());
		int col = xCoordToColumnNumber((int) evt.getX());
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

	private void refresh() throws IOException {
		App.setRoot("EditPage");
	}

	@FXML
	private void save() throws IOException {
		File file = App.getCurrentFile();
		if (file != null) {
			TerrainMapIO.terrainMapToJSON(App.getMap(), file);
		} else {
			saveAs();
		}
	}

	@FXML
	private void saveAs() throws IOException {
		File file = App.saveFile();
		if (file != null) {
			App.setCurrentFile(file);
			TerrainMapIO.terrainMapToJSON(App.getMap(), file);
		}
	}

	@FXML
	public void loadFile() {
		FileChooser loadChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)",
				"*.terrainmap");
		loadChooser.getExtensionFilters().add(filter);
		Stage loadWindow = new Stage(StageStyle.TRANSPARENT);
		File inputFile = loadChooser.showOpenDialog(loadWindow);

		if (inputFile != null) {
			try {
				TerrainMap map = TerrainMapIO.jsonToTerrainMap(inputFile);
				drawMap(map);
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open does not exist.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "Error opening file. Pleae make sure the file type is of '.terrainmap' ")
						.show();
			}
		}
	}

	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}
}