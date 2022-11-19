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
import java.util.Stack;
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
	private ToggleButton twoPointSelectToolButton;
	@FXML
	private ToggleButton raiseLowerToolButton;
    
	private TerrainMap map;
	private int numColors = 16;
	private int heightNum = 1;
	private int maxTileHeight = 99;
	private boolean isSaved = false;
	private Set<Tile> selectedTileSet = new HashSet<Tile>();
	private Stack<Tile> selectedTileStack = new Stack<Tile>();

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
		map.drawMap(editingCanvas, selectedTileSet, numColors);
	}
	
    private void drawFrontPerspective() {
    	map.drawFrontPerspective(editingCanvas, numColors);
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
		if (x >= (map.getNumColumns() * map.getTileSize())) {
			return -1;
		}
		int col = (int) (x / map.getTileSize());
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
		if (y >= (map.getNumRows() * map.getTileSize())) {
			return -1;
		}
		int row = (int) (y / map.getTileSize());
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
			} else if(App.getTool().equals("Two Point Select Tool")) {
				twoPointSelectTool(e);
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
		if(e.getButton().equals(MouseButton.PRIMARY)) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				Tile tile = map.getTileAt(row, col);
				selectedTileSet.add(tile);
				selectedTileStack.push(tile);
			}
		}
		else if(e.getButton().equals(MouseButton.SECONDARY)) {
			selectedTileSet.clear();
			selectedTileStack.clear();
		}
		refresh();
	}
	
	private void twoPointSelectTool(MouseEvent e) {
		int largestRow;
		int smallestRow;
		int largestCol;
		int smallestCol;
		selectTiles(e);
		if(selectedTileStack.size() > 1){
			Tile tile2 = selectedTileStack.pop();
			Tile tile1 = selectedTileStack.pop();
			if(tile2.getRow() >= tile1.getRow()) {
				largestRow = tile2.getRow();
				smallestRow = tile1.getRow();
			} else {
				largestRow = tile1.getRow();
				smallestRow = tile2.getRow();
			}
			if(tile2.getCol() >= tile1.getCol()) {
				largestCol = tile2.getCol();
				smallestCol = tile1.getCol();
			} else {
				largestCol = tile1.getCol();
				smallestCol = tile2.getCol();
			}
			for (int r = 0; r < map.getNumRows(); r++) {
				for (int c = 0; c < map.getNumColumns(); c++) {
					Tile tile = map.getTileAt(r, c);
					if(tile.getRow() >= smallestRow && tile.getRow() <= largestRow && tile.getCol() >= smallestCol && tile.getCol() <= largestCol) {
						selectedTileSet.add(tile);
					}
					
				}
			}
			selectedTileStack.clear();
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
		TerrainMapIO.confirmSave();
	}
	
	@FXML
	private void newFile() {
		//Will allow user to get a new terrain map to work on.
		TerrainMapIO.newFile();
	}

	@FXML
	private void save() throws IOException {
		TerrainMapIO.save();
	}

	@FXML
	private void saveAs() throws IOException {
		TerrainMapIO.saveAs();
	}

	@FXML
	public void loadFile() throws IOException {
		TerrainMapIO.loadFile();
		selectedTileSet.clear();
		refresh();
	}
	
	@FXML
	private void terrainMapToObj(ActionEvent event) throws IOException {
		TerrainMapIO.terrainMapToObj();
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
    public void twoPointSelectTool() {
    	App.setTool("Two Point Select Tool");
    	refresh();
    }
    
    @FXML
    public void heightTool() {
    	App.setTool("Height Tool");
    	refresh();
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