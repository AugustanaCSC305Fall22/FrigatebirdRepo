package frigatebird.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import com.jfoenix.controls.JFXToggleButton;

import frigatebird.ui.UndoRedoHandler;
import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.TerrainMapIO;
import frigatebird.terrainbuilder.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditorController {

	private UndoRedoHandler undoRedoHandler;
	private GridEditingCanvas editingCanvas;
	@FXML
	private BorderPane rootPane;
	@FXML
	private TabPane canvasTabPane;
	@FXML
	private JFXToggleButton heightToggleButton;
	@FXML
	private TextField heightNumTextField;
	@FXML
	private ToggleGroup toolButtonGroup;
	@FXML
	private JFXToggleButton selectToolButton;
	@FXML
	private JFXToggleButton twoPointSelectToolButton;
	@FXML
	private JFXToggleButton raiseLowerToolButton;
	@FXML
	private JFXToggleButton fillToolButton;
	@FXML
	private JFXToggleButton selectToggleButton;
	@FXML
	private JFXToggleButton multiSelectToggleButton;
	@FXML
	private TextField heightTileSelectInput;
	@FXML
	private TextField fillToolInput;
	@FXML
	private JFXToggleButton pasteToolButton;
	@FXML
	private ComboBox<String> featureType;

	private TerrainMap map;
	private String cutOrCopyString = "";
	private int numColors = 16;
	private int heightNum = 1;
	private int selectHeightNum = 0;
	private int maxTileHeight = 99;
	private int fillToolNum = 0;
	private Set<Tile> selectedTileSet = new HashSet<Tile>();
	private Stack<Tile> selectedTileStack = new Stack<Tile>();
	private Set<Tile> cutAndCopySet = new HashSet<Tile>();
	private Set<Tile> fillSet = new HashSet<Tile>();

	private ToolBox toolbox;

	@FXML
	private void initialize() {
		editingCanvas = new GridEditingCanvas(592,547);
		undoRedoHandler = new UndoRedoHandler(editingCanvas);
		
		//rootPane.setCenter(editingCanvas);
        Tab canvasTabOne = new Tab("Untitled" ,editingCanvas);
        canvasTabPane.getTabs().clear();
        canvasTabPane.getTabs().add(canvasTabOne);

         
        featureType.getItems().addAll("Pyramid", "Depression", "'Actual' Gate of hell", "Wave", "Pointy Building");
                

		map = App.getMap();
		toolbox = new ToolBox(ToolBox.Tool.SELECT);

		numColors = map.findMaxMapHeight() + 1;
		heightNumTextField.setText(Integer.toString(heightNum));
		heightTileSelectInput.setText(Integer.toString(selectHeightNum));
		fillToolInput.setText(Integer.toString(fillToolNum));
		editingCanvas.setOnMousePressed(e -> {
			try {
				handleCanvasClick(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		heightNumTextField.setOnKeyTyped(e -> setHeightNum(e));
		heightTileSelectInput.setOnKeyTyped(e -> setSelectHeightNum(e));
		fillToolInput.setOnKeyTyped(e -> setFillToolNum(e));
		if (App.getView().equals("Top Down View")) {
			drawMap();
		} else if (App.getView().equals("Side View")) {
			drawFrontPerspective();
		}
	}

	private void drawMap() {
		editingCanvas.drawMap(editingCanvas, selectedTileSet, numColors);
	}

	private void drawFrontPerspective() {
		editingCanvas.drawFrontPerspective(editingCanvas, numColors);
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
		if (x >= (map.getNumColumns() * editingCanvas.getTileSizeInPixels())) {
			return -1;
		}
		int col = (int) (x / editingCanvas.getTileSizeInPixels());
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
		if (y >= (map.getNumRows() * editingCanvas.getTileSizeInPixels())) {
			return -1;
		}
		int row = (int) (y / editingCanvas.getTileSizeInPixels());
		return row;
	}

	private void changeHeight(MouseEvent e) {
		if (selectedTileSet.isEmpty() && heightToggleButton.isSelected()) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			Tile tile = map.getTileAt(row, col);
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				changeHeightHelper(e, tile);
			}
		} else {
			for (Tile tile : selectedTileSet) {
				changeHeightHelper(e, tile);
			}
		}
		TerrainMapIO.setOpenSave(true);
		refresh();
		undoRedoHandler.saveState();
	}

	private void changeHeightHelper(MouseEvent e, Tile tile) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			if (tile.getHeight() + heightNum < maxTileHeight) {
				tile.setHeight(tile.getHeight() + heightNum);
			} else {
				tile.setHeight(maxTileHeight);
			}
		} else if (e.getButton().equals(MouseButton.SECONDARY)) {
			if (tile.getHeight() - heightNum > 0) {
				tile.setHeight(tile.getHeight() - heightNum);
			} else {
				tile.setHeight(0);
			}
		}
	}

	private void handleCanvasClick(MouseEvent e) throws IOException {
		if (App.getView().equals("Top Down View")) {
			if (toolbox.getCurrentTool().equals(ToolBox.Tool.HEIGHT)) {
				changeHeight(e);
			} else if (toolbox.getCurrentTool().equals(ToolBox.Tool.SELECT)) {
				selectTiles(e);
			} else if (toolbox.getCurrentTool().equals(ToolBox.Tool.TWO_POINT_SELECT)) {
				twoPointSelectTool(e);
			} else if (toolbox.getCurrentTool().equals(ToolBox.Tool.PASTE)) {
				pasteSelectedTiles(e);
			} else if (toolbox.getCurrentTool().equals(ToolBox.Tool.POINTY)) {
				pointyTilesTool(e);
			}
			else if(toolbox.getCurrentTool().equals(ToolBox.Tool.FILL)) {
				floodFill(e);
			}else if (toolbox.getCurrentTool().equals(ToolBox.Tool.FEATURE)) {
		        insertFeature(e);
		      }
		}
	}

	@FXML
	private void selectTilesAtHeight() {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				if (tile.getHeight() == selectHeightNum) {
					selectedTileSet.add(tile);
				}
			}
		}
		refresh();
	}

	private void setSelectHeightNum(KeyEvent e) {
		String text = heightTileSelectInput.getText();
		try {
			selectHeightNum = Integer.parseInt(text);
			if (selectHeightNum < 0) {
				selectHeightNum = 1;
				heightTileSelectInput.setText(Integer.toString(heightNum));
			}
		} catch (Exception exception) {

		}
	}

	private void setHeightNum(KeyEvent e) {
		String text = heightNumTextField.getText();
		try {
			heightNum = Integer.parseInt(text);
			if (heightNum > maxTileHeight) {
				heightNum = maxTileHeight;
				heightNumTextField.setText(Integer.toString(heightNum));
			} else if (heightNum < 1) {
				heightNum = 1;
				heightNumTextField.setText(Integer.toString(heightNum));
			}
		} catch (Exception exception) {

		}
	}

	private void setFillToolNum(KeyEvent e) {
		String text = fillToolInput.getText();
		try {
			fillToolNum = Integer.parseInt(text);
			if (fillToolNum > maxTileHeight) {
				fillToolNum = maxTileHeight;
				fillToolInput.setText(Integer.toString(fillToolNum));
			} else if (fillToolNum < 1) {
				fillToolNum = 1;
				fillToolInput.setText(Integer.toString(fillToolNum));
			}
		} catch (Exception exception) {

		}
	}

	private void selectTiles(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY) && selectToggleButton.isSelected()
				|| multiSelectToggleButton.isSelected()) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				Tile tile = map.getTileAt(row, col);
				selectedTileSet.add(tile);
				selectedTileStack.push(tile);
			}
		} else if (e.getButton().equals(MouseButton.SECONDARY)) {
			selectedTileSet.clear();
			selectedTileStack.clear();
		}
		refresh();
	}

	private void twoPointSelectTool(MouseEvent e) {
		if (multiSelectToggleButton.isSelected()) {
			int largestRow;
			int smallestRow;
			int largestCol;
			int smallestCol;
			selectTiles(e);
			if (selectedTileStack.size() > 1) {
				Tile tile2 = selectedTileStack.pop();
				Tile tile1 = selectedTileStack.pop();
				if (tile2.getRow() >= tile1.getRow()) {
					largestRow = tile2.getRow();
					smallestRow = tile1.getRow();
				} else {
					largestRow = tile1.getRow();
					smallestRow = tile2.getRow();
				}
				if (tile2.getCol() >= tile1.getCol()) {
					largestCol = tile2.getCol();
					smallestCol = tile1.getCol();
				} else {
					largestCol = tile1.getCol();
					smallestCol = tile2.getCol();
				}
				for (int r = 0; r < map.getNumRows(); r++) {
					for (int c = 0; c < map.getNumColumns(); c++) {
						Tile tile = map.getTileAt(r, c);
						if (tile.getRow() >= smallestRow && tile.getRow() <= largestRow && tile.getCol() >= smallestCol
								&& tile.getCol() <= largestCol) {
							selectedTileSet.add(tile);
						}

					}
				}
				selectedTileStack.clear();
			}
			refresh();
		}
	}

	private void cutAndCopyHelper(String cutOrCopy) {
		if (cutOrCopy.equals("cut")) {
			for (Tile tile : selectedTileSet) {
				Tile tileCopy = new Tile(tile.getHeight(), tile.getRow(), tile.getCol(), tile.getIsPointy());
				cutAndCopySet.add(tileCopy);
				tile.setHeight(0);
			}
		} else {
			for (Tile tile : selectedTileSet) {
				Tile tileCopy = new Tile(tile.getHeight(), tile.getRow(), tile.getCol(), tile.getIsPointy());
				cutAndCopySet.add(tileCopy);
			}
		}
	}

	@FXML
	private void cutSelectedTiles() {
		cutAndCopySet.clear();
		cutOrCopyString = "cut";
		cutAndCopyHelper(cutOrCopyString);
		selectedTileSet.clear();
		refresh();
		undoRedoHandler.saveState();
	}

	@FXML
	private void copySelectedTiles() {
		cutAndCopySet.clear();
		cutOrCopyString = "copy";
		cutAndCopyHelper(cutOrCopyString);
		selectedTileSet.clear();
		refresh();
		undoRedoHandler.saveState();
	}

	@FXML
	private void pasteSelectedTiles(MouseEvent e) {
		if (!(cutAndCopySet.isEmpty())) {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				int rowDiff = 0;
				int colDiff = 0;
				int row = yCoordToRowNumber((int) e.getY());
				int col = xCoordToColumnNumber((int) e.getX());
				int rowColComboNum = 1000;
				for (Tile tile : cutAndCopySet) {
					if (tile.getRow() + tile.getCol() < rowColComboNum) {
						rowColComboNum = tile.getRow() + tile.getCol();
						rowDiff = row - tile.getRow();
						colDiff = col - tile.getCol();
					}
				}
				for (Tile tile : cutAndCopySet) {
					int newRowNum = tile.getRow() + rowDiff;
					int newColNum = tile.getCol() + colDiff;
					if (newRowNum >= 0 && newRowNum < map.getNumRows() && newColNum >= 0
							&& newColNum < map.getNumColumns()) {
						Tile mapTile = map.getTileAt(newRowNum, newColNum);
						mapTile.setHeight(tile.getHeight());
					}
				}
			}
			if (cutOrCopyString.equals("cut")) {
				cutAndCopySet.clear();
			}
		}
		refresh();
		undoRedoHandler.saveState();
	}

	private void tileToFillSearcher(int row, int col, int fillHeight, int targetTileHeight) {
		if (row < 0 || row >= map.getNumRows() || col < 0 || col >= map.getNumColumns()
				|| map.getTileAt(row, col).getHeight() != targetTileHeight || map.getTileAt(row, col).getHeight() == fillHeight) {
			return;
		} else {
				map.getTileAt(row, col).setHeight(fillHeight);
				tileToFillSearcher(row + 1, col, fillHeight, targetTileHeight);
				tileToFillSearcher(row - 1, col, fillHeight, targetTileHeight);
				tileToFillSearcher(row, col + 1, fillHeight, targetTileHeight);
				tileToFillSearcher(row, col - 1, fillHeight, targetTileHeight);
			}
			refresh();
		}
		
			private void floodFill(MouseEvent e) {
				int row = yCoordToRowNumber((int) e.getY());
				int col = xCoordToColumnNumber((int) e.getX());
				int targetTileHeight = map.getTileAt(row, col).getHeight();
				
				int fillHeight = Integer.parseInt(fillToolInput.getText());
				
				if (e.getButton().equals(MouseButton.PRIMARY) && fillToolButton.isSelected()) {
					tileToFillSearcher(row, col, fillHeight, targetTileHeight);
				}
			for(Tile tile: fillSet) {
				tile.setHeight(fillToolNum);
			}
			fillSet.clear();
		refresh();
		undoRedoHandler.saveState();
	}

	private void fillFromTile(Tile initialTile) {
		Set<Tile> fillSetSecondary = new HashSet<Tile>();
		int loopCount = 1;
		int initialTileRow = initialTile.getRow();
		int initialTileCol = initialTile.getCol();
		int initialTileHeight = initialTile.getHeight();
		while ((initialTileCol + loopCount < map.getNumColumns())
				&& map.getTileAt(initialTileRow, initialTileCol + loopCount).getHeight() == initialTileHeight) {
			Tile tile = map.getTileAt(initialTileRow, initialTileCol + loopCount);
			fillSet.add(tile);
			loopCount++;
		}
		loopCount = 0;
		while ((initialTileCol - loopCount >= 0)
				&& map.getTileAt(initialTileRow, initialTileCol - loopCount).getHeight() == initialTileHeight) {
			Tile tile = map.getTileAt(initialTileRow, initialTileCol - loopCount);
			fillSet.add(tile);
			loopCount++;
		}
		loopCount = 0;
		for (Tile horizontalFillTile : fillSet) {
			while ((horizontalFillTile.getRow() + loopCount < map.getNumRows())
					&& map.getTileAt(horizontalFillTile.getRow() + loopCount, horizontalFillTile.getCol())
							.getHeight() == initialTileHeight) {
				Tile tile = map.getTileAt(horizontalFillTile.getRow() + loopCount, horizontalFillTile.getCol());
				fillSetSecondary.add(tile);
				loopCount++;
			}
			loopCount = 0;
			while ((horizontalFillTile.getRow() - loopCount >= 0)
					&& map.getTileAt(horizontalFillTile.getRow() - loopCount, horizontalFillTile.getCol())
							.getHeight() == initialTileHeight) {
				Tile tile = map.getTileAt(horizontalFillTile.getRow() - loopCount, horizontalFillTile.getCol());
				fillSetSecondary.add(tile);
				loopCount++;
			}
			loopCount = 0;
		}
		for (Tile tile : fillSetSecondary) {
			fillSet.add(tile);
		}
		fillSetSecondary.clear();
	}

	private void pointyTilesTool(MouseEvent e) {
		if (e.getButton().equals(MouseButton.PRIMARY)) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				Tile tile = map.getTileAt(row, col);
				// selectedTileSet.add(tile);
				tile.setIsPointy(true);
			}
		} else if (e.getButton().equals(MouseButton.SECONDARY)) {
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			if (row >= 0 && row < map.getNumRows() && col >= 0 && col < map.getNumColumns()) {
				Tile tile = map.getTileAt(row, col);
				// selectedTileSet.remove(tile);
				tile.setIsPointy(false);
			}
		}
		refresh();
		undoRedoHandler.saveState();
	}

	private void refresh() {
		this.map = App.getMap();
		numColors = map.findMaxMapHeight() + 1;
		//selectedTileSet = map.getSelectedTileSet();
		if(App.getView().equals("Top Down View")) {
			drawMap();
		} else if (App.getView().equals("Side View")) {
			drawFrontPerspective();
		}
	}

	public void confirmSave() throws IOException {
		TerrainMapIO.confirmSave();
	}

	@FXML
	private void newFile() {
		// Will allow user to get a new terrain map to work on.
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
		toolbox.setCurrentTool(ToolBox.Tool.SELECT);
	}

	@FXML
	public void twoPointSelectTool() {
		toolbox.setCurrentTool(ToolBox.Tool.TWO_POINT_SELECT);
	}

	@FXML
	public void heightTool() {
		toolbox.setCurrentTool(ToolBox.Tool.HEIGHT);
	}

	@FXML
	public void pasteTool() {
		toolbox.setCurrentTool(ToolBox.Tool.PASTE);
	}

	@FXML
	public void fillTool() {
		toolbox.setCurrentTool(ToolBox.Tool.FILL);
	}

	@FXML
	public void pointyTiles() {
		toolbox.setCurrentTool(ToolBox.Tool.POINTY);
	}

	@FXML
	private void randomizeTiles() {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				Random randGen = new Random();
				tile.setHeight(randGen.nextInt(maxTileHeight + 1));
			}
		}
		refresh();
		undoRedoHandler.saveState();
	}

	private String insertFeatureHelper(String Type) {
		String filePath = "";
		if (Type == "Pyramid") {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\pyramid.terrainmap";
		}
		if (Type == "Depression") {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\depression.terrainmap";
		}
		if (Type == "Wave") {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\wave.terrainmap";
		}
		if (Type == "'Actual' Gate of hell") {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\gate.terrainmap";
		}
		if (Type == "Pointy Building") {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\buildingPointy.terrainmap";
		}
		
		return filePath;
	}
	
	private int featureSizeAdjustment() {
		int sizeAdj = 0;
		if(map.getNumColumns()<50 && map.getNumColumns() > 40 &&  map.getNumRows()<50 && map.getNumRows() > 40) {
			sizeAdj = 1; 
		}
		if(map.getNumColumns()<=40 && map.getNumColumns() > 30 &&  map.getNumRows()<=40 && map.getNumRows() > 30) {
			sizeAdj = 2; 
		}if(map.getNumColumns()<=30 && map.getNumColumns() > 20 &&  map.getNumRows()<=30 && map.getNumRows() > 20) {
			sizeAdj = 3; 
		}if(map.getNumColumns()<=20 && map.getNumColumns() > 10 &&  map.getNumRows()<=20 && map.getNumRows() > 10) {
			sizeAdj = 4; 
		}if(map.getNumColumns()<=10 && map.getNumColumns() > 5 &&  map.getNumRows()<=10 && map.getNumRows() > 5) {
			sizeAdj = 5; 
		}else {
			int minSize = 0;
			if(map.getNumColumns() > map.getNumRows() + 10) {
				minSize = map.getNumRows();
			}if(map.getNumRows() > map.getNumColumns() + 10) {
				minSize = map.getNumColumns();
			}
			
			if(minSize < 50 && minSize > 40) {
				sizeAdj = 1;
			}
			if(minSize <= 40 && minSize > 30) {
				sizeAdj = 2;
			}
			if(minSize <= 30 && minSize > 20) {
				sizeAdj = 3;
			}
			if(minSize <= 20 && minSize > 10) {
				sizeAdj = 4;
			}
			if(minSize <= 10 && minSize > 5) {
				sizeAdj = 5;
			}
			
		}
		
		return sizeAdj;
		
	}


	private void insertFeature(MouseEvent e) throws IOException {

	
			String type = featureType.getValue();
			int row = yCoordToRowNumber((int) e.getY());
			int col = xCoordToColumnNumber((int) e.getX());
			Tile initialTile = map.getTileAt(row, col);
			File file = new File(insertFeatureHelper(type));
            
			try {
			TerrainMap feature = TerrainMapIO.jsonToTerrainMap(file);

			int midRow = feature.getNumRows() / 2;
			int midCol = feature.getNumColumns() / 2 + 1;
            int trimSize = featureSizeAdjustment();
			
			
           
				int colIncrement = 1 ;
				int rowIncrement = -midCol + trimSize;
				for (int r = trimSize; r < feature.getNumRows()-trimSize; r++) {
					colIncrement = 0;
					rowIncrement++;
					
					for (int c = trimSize; c < feature.getNumColumns()-trimSize; c++) {
						Tile tile = feature.getTileAt(r, c);
						int height = tile.getHeight();
						
                        
						Tile tileOnMap = map.getTileAt(initialTile.getRow() + rowIncrement,
								initialTile.getCol() + colIncrement - midRow + trimSize);
						 {
							 
							    if(tile.getIsPointy() == true) {
							    	tileOnMap.setIsPointy(true);
							    }if(tile.getHeight() > 0) {
								tileOnMap.setHeight(height);
							    }
								colIncrement++;
							}
						 
						}
					
				
				
		}
           }catch(IndexOutOfBoundsException e1) {
        	   new Alert(AlertType.ERROR, "Not enough real estate here to build this feature.").showAndWait();
           }
        refresh();
        undoRedoHandler.saveState();
      }
    
    @FXML
	private void menuEditUndo() {
    	selectedTileSet.clear();
		undoRedoHandler.undo();
		refresh();
	}

	@FXML
	private void menuEditRedo() {
		selectedTileSet.clear();
		undoRedoHandler.redo();
		refresh();
	}
    
    @FXML
    void openPreviewPage(ActionEvent event) throws IOException {
  	  MapPreviewController threeDMap = new MapPreviewController(App.getMap()); 
   	  threeDMap.start(threeDMap.getStage());
    }
    
    @FXML
	private void switchToAboutScreen() throws IOException {
		App.setRoot("AboutScreen");
	}

	@FXML
	private void AdditionOfFeature() {
		toolbox.setCurrentTool(ToolBox.Tool.FEATURE);
		heightToggleButton.setSelected(false);
	}

	@FXML
	private void exitAction() throws IOException {
		Platform.exit();
	}

	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}
	
	@FXML
	private void zoomIn() {
		editingCanvas.setScaleX(editingCanvas.getScaleX() * 1.2);
		editingCanvas.setScaleY(editingCanvas.getScaleY() * 1.2);
	}
	
	@FXML
	private void zoomOut() {
		editingCanvas.setScaleX(editingCanvas.getScaleX() * 0.8);
		editingCanvas.setScaleY(editingCanvas.getScaleY() * 0.8);
	}
}