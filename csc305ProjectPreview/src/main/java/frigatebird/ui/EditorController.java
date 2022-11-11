package frigatebird.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.TerrainMapIO;
import frigatebird.terrainbuilder.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditorController {

	@FXML
	private Canvas editingCanvas;	
	@FXML
	private TextField heightNumTextField;
	@FXML
	private ToggleGroup toolButtonGroup;
	@FXML
	private ToggleButton selectToolButton;
	@FXML
	private ToggleButton raiseLowerToolButton;
    
	private TerrainMap map;
	private int tileSize = 30;
	private int numColors = 16;
	private int heightNum = 1;
	private int maxTileHeight = 15;
	private boolean isSaved = false;
	private Set<Tile> selectedTileSet = new HashSet<Tile>();

	@FXML
	private void initialize() {
		this.map = App.getMap();
		numColors = findMaxMapHeight() + 1;
		heightNumTextField.setText(Integer.toString(heightNum));
		editingCanvas.setOnMousePressed(e -> handleCanvasClick(e));
		heightNumTextField.setOnKeyTyped(e -> handleTextFieldEvent(e));
		if(App.getView().equals("Top Down View")) {
			drawMap();
		}
		else if(App.getView().equals("Side View")) {
			drawFrontPerspective();
		}
	}


	private void drawMap() {
		GraphicsContext gc = editingCanvas.getGraphicsContext2D();
		Color color = Color.rgb(245, 245, 245);
		gc.setFill(color);
		gc.fillRect(0, 0, 1000, 1000);
		drawMapTiles(gc);
		gc.setFill(Color.hsb(300, 1, 1));
		for(Tile tile: selectedTileSet) {
			gc.fillRect(tile.getCol() * tileSize, tile.getRow() * tileSize, tileSize-1, tileSize-1);
		}
		drawMapNumbers(gc);
		
	}
	
	private void drawMapTiles(GraphicsContext gc) {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				double saturation = 1 - (double) height/numColors;
				double brightness = (double) height/numColors;
				Color color = Color.hsb(230, saturation, brightness);
				gc.setFill(color);
				double x = c * tileSize;
				double y = r * tileSize;
				gc.fillRect(x, y, tileSize - 1, tileSize - 1);
			}
		}
	}
	
	private void drawMapNumbers(GraphicsContext gc) {
		Font font = Font.loadFont("file:src/main/resources/frigatebird/Fonts/Majoris_Italic.ttf", 14);
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
	
    public void drawFrontPerspective() {
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
				double x = c * tileSize;
				double y = r * tileSize;

		    	map.getTileAt(r, c);
		    	gc.setFill(Color.WHITE);
				gc.fillRect(x, y, tileSize-1, tileSize-1);
				
				if(height > max) {
					max = height;
				}
			}
			
			heightList.add(max);
			max = 0;
		}
    	int index = heightList.size()-1;
  
		for(int c = 0; c < map.getNumColumns(); c++) {
			for(int r = index ; r > index - heightList.get(c); r--) {
				double x = c * tileSize;
				double y = r * tileSize;
		    	
		    	map.getTileAt(r, c);
				gc.setFill(Color.BLACK);
				gc.fillRect(x, y, tileSize-1, tileSize-1);
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
	
	private int findMaxMapHeight() {
		int max = 0;
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				if(max < tile.getHeight()) {
					max = tile.getHeight();
				}
			}
		}
		return max;
	}

	private void changeHeight(MouseEvent e) {
		if(selectedTileSet.isEmpty()) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			Tile tile = map.getTileAt(row, col);
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				changeHeightHelper(e, tile);	
			}
		}
		else {
			for(Tile tile: selectedTileSet) {
				changeHeightHelper(e, tile);
			}
		}
		refresh();
	}
	
	private void changeHeightHelper(MouseEvent e, Tile tile) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			if(tile.getHeight() + heightNum < maxTileHeight) {
				tile.setHeight(tile.getHeight() + heightNum);
			}
			else {
				tile.setHeight(maxTileHeight);
			}
		} else if (e.getButton().equals(MouseButton.SECONDARY)) {
			if(tile.getHeight() - heightNum > 0) {
				tile.setHeight(tile.getHeight() - heightNum);
			}
			else {
				tile.setHeight(0);
			}
		}
	}
	
	private void handleCanvasClick(MouseEvent e) {
		if(App.getView().equals("Top Down View")) {
			if(App.getTool().equals("Height Tool")) {
				changeHeight(e);
			}
			else if(App.getTool().equals("Select Tool")) {
				selectTiles(e);
			}
		}
	}
	
	private void handleTextFieldEvent(KeyEvent e) {
		setHeightNum(e);
	}
	
	private void setHeightNum(KeyEvent e) {
    	String text = heightNumTextField.getText();
    	try {
    		heightNum = Integer.parseInt(text);
    		if(heightNum > maxTileHeight) {
    			heightNum = maxTileHeight;
    			heightNumTextField.setText(Integer.toString(heightNum));
    		}
    		else if(heightNum < 1) {
    			heightNum = 1;
    			heightNumTextField.setText(Integer.toString(heightNum));
    		}
    	}
    	catch(Exception exception) {
    		
    	}
    }

	private void selectTiles(MouseEvent e) {
		if(App.getView() == "Side View") {
			new Alert(AlertType.ERROR, "Revert back to Top Down View to make more changes.").show();
		}
		else if(e.getButton().equals(MouseButton.PRIMARY)) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				Tile tile = map.getTileAt(row, col);
				selectedTileSet.add(tile);
			}
		}
		else if(e.getButton().equals(MouseButton.SECONDARY)) {
			selectedTileSet.clear();
		}
		refresh();
	}

	private void refresh(){
		this.map = App.getMap();
		numColors = findMaxMapHeight() + 1;
		if(App.getView().equals("Top Down View")) {
			drawMap();
		}
		else if(App.getView().equals("Side View")) {
			drawFrontPerspective();
		}
	}
	
	public void confirmSave() throws IOException {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("You forgot to save!");
		confirm.setContentText("Would you like to save the changes made?");
		Optional<ButtonType> answer = confirm.showAndWait();
		
		if(answer.get() == ButtonType.OK) {
			saveAs();
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
		if(!isSaved) {
			confirmSave();
		}
		FileChooser loadChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)", "*.terrainmap");
		loadChooser.getExtensionFilters().add(filter);
		Stage loadWindow = new Stage(StageStyle.TRANSPARENT);
		File inputFile = loadChooser.showOpenDialog(loadWindow);

		if (inputFile != null) {
			try {
				App.setCurrentFile(inputFile);
				App.setMap(TerrainMapIO.jsonToTerrainMap(inputFile));
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open does not exist.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "Error opening file. Please make sure the file type is of '.terrainmap' ").show();
			}
		}
		selectedTileSet.clear();
		refresh();
	}
	
	@FXML
    public void topDownView() {
		App.setView("Top Down View");
		refresh();
	}
	
    @FXML
    public void sideView() {
    	App.setView("Side View");
		refresh();
	}
    
    @FXML
    public void selectTool() {
    	App.setTool("Select Tool");
    	refresh();
    }
    
    @FXML
    public void heightTool() {
    	App.setTool("Height Tool");
    	refresh();
    }
    
    @FXML
	private void terrainMapToObj(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Wavefront (*.obj)", "*.obj");
		fileChooser.getExtensionFilters().add(filter);
		Stage saveObjWindow = new Stage(StageStyle.TRANSPARENT);
		File file = fileChooser.showSaveDialog(saveObjWindow);
		if (file != null) {
			App.setCurrentFile(file);
			createObjFile(App.getMap(), file);
		}
	}

	public void createObjFile(TerrainMap map, File objFile) throws IOException {
		FileWriter writer = new FileWriter(objFile);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				int height = map.getTileAt(r, c).getHeight();

				writer.write("v" + " " + r + " " + c + " " + height + "\n");
				writer.write("v" + " " + r + " " + c + " " + 0 + "\n");
				writer.write("v" + " " + r + " " + (c + 1) + " " + 0 + "\n");
				writer.write("v" + " " + r + " " + (c + 1) + " " + height + "\n");
				writer.write("v" + " " + (r + 1) + " " + c + " " + height + "\n");
				writer.write("v" + " " + (r + 1) + " " + c + " " + 0 + "\n");
				writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + 0 + "\n");
				writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + height + "\n");
			}
		}
		int faceIncrement = 0;
		for (int i = 0; i < map.getNumRows(); i++) {
			for (int j = 0; j < map.getNumColumns(); j++) {
				writer.write("f" + " " + (4 + faceIncrement) + " " + (3 + faceIncrement) + " " + (2 + faceIncrement)
						+ " " + (1 + faceIncrement) + "\n");
				writer.write("f" + " " + (2 + faceIncrement) + " " + (6 + faceIncrement) + " " + (5 + faceIncrement)
						+ " " + (1 + faceIncrement) + "\n");
				writer.write("f" + " " + (3 + faceIncrement) + " " + (7 + faceIncrement) + " " + (6 + faceIncrement)
						+ " " + (2 + faceIncrement) + "\n");
				writer.write("f" + " " + (8 + faceIncrement) + " " + (7 + faceIncrement) + " " + (3 + faceIncrement)
						+ " " + (4 + faceIncrement) + "\n");
				writer.write("f" + " " + (5 + faceIncrement) + " " + (8 + faceIncrement) + " " + (4 + faceIncrement)
						+ " " + (1 + faceIncrement) + "\n");
				writer.write("f" + " " + (6 + faceIncrement) + " " + (7 + faceIncrement) + " " + (8 + faceIncrement)
						+ " " + (5 + faceIncrement) + "\n");
				faceIncrement += 8;
			}

		}
		writer.close();

	}
    
    @FXML
    private void randomizeTiles() {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				Random randGen = new Random();
				tile.setHeight(randGen.nextInt(16));
			}
		}
		refresh();
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