package frigatebird.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

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
import javafx.scene.control.ButtonType;
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
	private boolean isSaved = false;

	@FXML
	private void initialize() {
		this.map = App.getMap();
		editingCanvas.setOnMousePressed(e -> changeHeight(e, 1));
		if(App.getView().equals("Top Down View")) {
			drawMap();
		}
		else if(App.getView().equals("Side View")) {
			drawFrontPerspective();
		}
	}

	private void drawMap() {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
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
	
    public void drawFrontPerspective() {
    	ArrayList<Integer> heightList = new ArrayList<>();
		int height = 0;
		int max = Integer.MIN_VALUE;
		for(int c = 0; c < map.getNumColumns(); c++) {
			for(int r = 0; r < map.getNumRows(); r++) {
				height = map.getTileAt(r, c).getHeight();
				if(height > max) {
					max = height;
				}
			}
			heightList.add(max);
			max = 0;
		}
    	GraphicsContext gc = editingCanvas.getGraphicsContext2D();
    	int index = heightList.size()-1;
  
		for(int c = 0; c < map.getNumColumns(); c++) {
			for(int r = index ; r > index - heightList.get(c); r--) {
				double x = c * tileSize;
				double y = r * tileSize;
		    	
		    	map.getTileAt(r, c);
				gc.fillRect(x, y, tileSize-1, tileSize-1);
				gc.setFill(Color.BLACK);
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
		if(App.getView().equals("Top Down View") && App.getTool().equals("Height Tool")) {
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
					e.printStackTrace();
				}
			}
		}
	}

	private void refresh() throws IOException {
		App.setRoot("EditPage");
	}
	
	public void confirmSave(String caller) throws IOException {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("You forgot to save!");
		confirm.setContentText("Would you like to save the chanes made?");
		Optional<ButtonType> answer = confirm.showAndWait();
		
		if(answer.get() == ButtonType.OK) {
			saveAs();
		}
		else {
			isSaved = true;
			 if(caller.equals("load")) {
				 loadFile();
			 }
			 else {
				 newFile(); //Has not yet been implemented.
			 }
		}
	}
	
	
	@FXML
	private void newFile() {
		//Will allow user to get a new terrain map to work on. 
	}

	@FXML
	private void save() throws IOException {
		File file = App.getCurrentFile();
		if (file != null) {
			TerrainMapIO.terrainMapToJSON(App.getMap(), file);
			isSaved = true;
		} else {
			saveAs();
		}
	}

	@FXML
	private void saveAs() throws IOException {
		FileChooser fileChooser = new FileChooser();
    	FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)", "*.terrainmap");
        fileChooser.getExtensionFilters().add(filter);
		Stage saveWindow = new Stage(StageStyle.TRANSPARENT);
    	File file = fileChooser.showSaveDialog(saveWindow);
		if (file != null) {
			App.setCurrentFile(file);
			TerrainMapIO.terrainMapToJSON(App.getMap(), file);
			isSaved = true;
		}
	}

	@FXML
	public void loadFile() throws IOException{
		if(isSaved) {
			FileChooser loadChooser = new FileChooser();
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)",
					"*.terrainmap");
			loadChooser.getExtensionFilters().add(filter);
			Stage loadWindow = new Stage(StageStyle.TRANSPARENT);
			File inputFile = loadChooser.showOpenDialog(loadWindow);
	
			if (inputFile != null) {
				try {
					map = TerrainMapIO.jsonToTerrainMap(inputFile);
					drawMap();
					App.setMap(map);
				} catch (FileNotFoundException ex) {
					new Alert(AlertType.ERROR, "The file you tried to open does not exist.").showAndWait();
				} catch (IOException ex) {
					new Alert(AlertType.ERROR, "Error opening file. Please make sure the file type is of '.terrainmap' ").show();
				}
			}
		}
		else {
			confirmSave("load");
		}
	}
	
	
	
	
	@FXML
    void topDownView() throws IOException {
		App.setView("Top Down View");
		refresh();
	}
	
    @FXML
    void sideView() throws IOException {
    	App.setView("Side View");
		refresh();
	}
    
    @FXML
    private void randomizeTiles() {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				Random randGen = new Random();
				tile.setHeight(randGen.nextInt(16));
				try {
					refresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    
    @FXML
	private void switchToAboutScreen() throws IOException {
		App.setRoot("AboutScreen");
	}
    
	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}
	
}