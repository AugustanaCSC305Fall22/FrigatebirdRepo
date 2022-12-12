package frigatebird.terrainbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.gson.*;

import frigatebird.ui.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Deals with saving, loading, and exporting of TerrainMap objects
 */
public class TerrainMapIO {

	private static boolean openSave = false;
	private static List<String> savedMapNames = new ArrayList<String>();
	private static Set<String> templateMapNames = new HashSet<String>();
	private static double rad = 1; 
    private static double n = Math.sqrt(rad * rad * 0.75); 
    private static double tileHeight = 2 * rad;
    private static double tileWidth = 2 * n;
    private static double shift = n / Math.sqrt(3);
	
	private static void terrainMapToJSON(TerrainMap map, File outputFile) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(outputFile);
		gson.toJson(map, writer);
		writer.close();
	}

	/**
	 * Converts json text files to a saved TerrainMap object
	 * 
	 * @param inputFile - a text file storing TerrainMap save info in the form of a json
	 * @return - the saved TerrainMap object found from the input json file
	 * @throws JsonSyntaxException - exception thrown when gson tries to read a malformed json file
	 * @throws JsonIOException - exception when gson is unable to read or write to an input stream
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static TerrainMap jsonToTerrainMap(File inputFile) throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(inputFile);
		TerrainMap map = gson.fromJson(reader, TerrainMap.class);
		reader.close();
		return map;
	}

	/**
	 * Saves a TerrainMap object to an existing text file json
	 * 
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static void save() throws IOException {
		File file = App.getCurrentFile();
		File saveFile = new File("src\\main\\resources\\frigatebird\\SavedMaps\\" + App.getMap().getName() + ".terrainmap");
		initializeTemplates();
		if(file != null) {
			System.out.println(file.getName());
			if(templateMapNames.contains(file.getName())) {
				saveAs();
			}
			else if (file != null) {
				savedMapNames.add(App.getMap().getName());
				terrainMapToJSON(App.getMap(), file);
				terrainMapToJSON(App.getMap(), saveFile);
			}
		}
		else {
			saveAs();
		}
	}

	/**
	 * Saves a TerrainMap object to a new json text file that it creates and names
	 * 
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static void saveAs() throws IOException {
		FileChooser fileChooser = new FileChooser(); 
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)",
				"*.terrainmap");
		fileChooser.setInitialFileName(App.getMap().getName());
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setInitialDirectory(App.getDirectory());
		Stage saveWindow = new Stage(StageStyle.TRANSPARENT);
		File file = fileChooser.showSaveDialog(saveWindow);
		savedMapNames.add(App.getMap().getName());
		File saveFile = new File("src\\main\\resources\\frigatebird\\SavedMaps\\" + App.getMap().getName() + ".terrainmap");
		if (file != null) {
			App.setCurrentFile(file);
			if(file.getParent() != null) {
				App.setDirectory(new File(file.getParent()));
			}
			terrainMapToJSON(App.getMap(), file);
			terrainMapToJSON(App.getMap(), saveFile);
			openSave = false;
		}
	}

	/**
	 * Displays an alert to the user in the case that they have not saved and will create a save
	 * 
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static void confirmSave() throws IOException {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("You forgot to save!");
		confirm.setContentText("Would you like to save the changes made?");
		Optional<ButtonType> answer = confirm.showAndWait();

		if (answer.get() == ButtonType.OK) {
			saveAs();
		}
	}

	/**
	 * Loads a selected TerrainMap using json and makes it the current map
	 * 
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static void loadFile() throws IOException {
		if (openSave) {
			confirmSave();
		}
		FileChooser loadChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)",
				"*.terrainmap");
		loadChooser.getExtensionFilters().add(filter);
		loadChooser.setInitialDirectory(App.getDirectory());
		Stage loadWindow = new Stage(StageStyle.TRANSPARENT);
		File inputFile = loadChooser.showOpenDialog(loadWindow);

		if (inputFile != null) {
			try {
				App.setCurrentFile(inputFile);
				if(inputFile.getParent() != null) {
					App.setDirectory(new File(inputFile.getParent()));
				}
				App.setMap(TerrainMapIO.jsonToTerrainMap(inputFile));
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open does not exist.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "Error opening file. Please make sure the file type is of '.terrainmap' ")
						.show();
			}
		}
	}

	/**
	 * Creates an .obj object using a TerrainMap object and file
	 * 
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public static void terrainMapToObj() throws IOException {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Wavefront (*.obj)", "*.obj");
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setInitialDirectory(App.getDirectory());
		Stage saveObjWindow = new Stage(StageStyle.TRANSPARENT);
		File file = fileChooser.showSaveDialog(saveObjWindow);
		if (file != null) {
			App.setCurrentFile(file);
			if(file.getParent() != null) {
				App.setDirectory(new File(file.getParent()));
			}
			if(App.getMap().isHexagonal()) {
				createHexObjFile(App.getMap(), file);
			}else {
				createObjFile(App.getMap(), file);
			}
		}
	}
	
	private static void createHexObjFile(TerrainMap map, File objFile) throws IOException {
		FileWriter writer = new FileWriter(objFile);
		double y = rad * tileHeight * 0.75;
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				double x = c * tileWidth;
				if(r % 2 == 1) {
					x += n;
				}
				double[] xCoords = {x, x, x + n, x + tileWidth, x + tileWidth, x + n};
		        double[] yCoords = {y + shift, y + rad + shift, y + rad * 1.5 + shift, y + rad + shift, y + shift, y - rad * 0.5 + shift};
				int height = tile.getHeight();
				
				writer.write("v" + " " + xCoords[0] + " " + yCoords[0] + " " + 0 + "\n");
				writer.write("v" + " " + xCoords[1] + " " + yCoords[1] + " " + 0 + "\n");
				writer.write("v" + " " + xCoords[2] + " " + yCoords[2] + " " + 0 + "\n");
				writer.write("v" + " " + xCoords[3] + " " + yCoords[3] + " " + 0 + "\n");
				writer.write("v" + " " + xCoords[4] + " " + yCoords[4] + " " + 0 + "\n");
				writer.write("v" + " " + xCoords[5] + " " + yCoords[5] + " " + 0 + "\n");
				
				writer.write("v" + " " + xCoords[0] + " " + yCoords[0] + " " + height + "\n");
				writer.write("v" + " " + xCoords[1] + " " + yCoords[1] + " " + height + "\n");
				writer.write("v" + " " + xCoords[2] + " " + yCoords[2] + " " + height + "\n");
				writer.write("v" + " " + xCoords[3] + " " + yCoords[3] + " " + height + "\n");
				writer.write("v" + " " + xCoords[4] + " " + yCoords[4] + " " + height + "\n");
				writer.write("v" + " " + xCoords[5] + " " + yCoords[5] + " " + height + "\n");
			}
			y += tileHeight - rad;
		}
		
		int vertexCount = 0;
		for (int i = 0; i < map.getNumRows(); i++) {
			for (int j = 0; j < map.getNumColumns(); j++) {
				Tile tile = map.getTileAt(i, j);
				writer.write("f" + " " + (1 + vertexCount) + " " + (2 + vertexCount) + " " + (3 + vertexCount)
						+ " " + (4 + vertexCount) + " " + (5 + vertexCount) + " " + (6 + vertexCount) + "\n");
				
				writer.write("f" + " " + (1 + vertexCount) + " " + (7 + vertexCount) + " " + (8 + vertexCount)
						+ " " + (2 + vertexCount) + "\n");
				writer.write("f" + " " + (2 + vertexCount) + " " + (8 + vertexCount) + " " + (9 + vertexCount)
						+ " " + (3 + vertexCount) + "\n");
				writer.write("f" + " " + (3 + vertexCount) + " " + (9 + vertexCount) + " " + (10 + vertexCount)
						+ " " + (4 + vertexCount) + "\n");
				writer.write("f" + " " + (4 + vertexCount) + " " + (10 + vertexCount) + " " + (11 + vertexCount)
						+ " " + (5 + vertexCount) + "\n");
				writer.write("f" + " " + (5 + vertexCount) + " " + (11 + vertexCount) + " " + (12 + vertexCount)
						+ " " + (6 + vertexCount) + "\n");
				writer.write("f" + " " + (6 + vertexCount) + " " + (12 + vertexCount) + " " + (7 + vertexCount)
						+ " " + (1 + vertexCount) + "\n");
				
				writer.write("f" + " " + (12 + vertexCount) + " " + (11 + vertexCount) + " " + (10 + vertexCount)
						+ " " + (9 + vertexCount) + " " + (8 + vertexCount) + " " + (7 + vertexCount) + "\n");
				vertexCount += 12;

				}
			}
		writer.close();
		
	}

	private static void createObjFile(TerrainMap map, File objFile) throws IOException {
		FileWriter writer = new FileWriter(objFile);
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();

				writer.write("v" + " " + r + " " + c + " " + height + "\n");
				writer.write("v" + " " + r + " " + c + " " + 0 + "\n");
				writer.write("v" + " " + r + " " + (c + 1) + " " + 0 + "\n");
				writer.write("v" + " " + r + " " + (c + 1) + " " + height + "\n");
				writer.write("v" + " " + (r + 1) + " " + c + " " + height + "\n");
				writer.write("v" + " " + (r + 1) + " " + c + " " + 0 + "\n");
				writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + 0 + "\n");
				writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + height + "\n");
				if (tile.getIsPointy()) {
					writer.write("v" + " " + (r + 0.5) + " " + (c + 0.5) + " " + (height + 3) + "\n");
				}
			}
		}
		int vertexCount = 0;
		for (int i = 0; i < map.getNumRows(); i++) {
			for (int j = 0; j < map.getNumColumns(); j++) {
				Tile tile = map.getTileAt(i, j);
				writer.write("f" + " " + (4 + vertexCount) + " " + (3 + vertexCount) + " " + (2 + vertexCount)
						+ " " + (1 + vertexCount) + "\n");
				writer.write("f" + " " + (2 + vertexCount) + " " + (6 + vertexCount) + " " + (5 + vertexCount)
						+ " " + (1 + vertexCount) + "\n");
				writer.write("f" + " " + (3 + vertexCount) + " " + (7 + vertexCount) + " " + (6 + vertexCount)
						+ " " + (2 + vertexCount) + "\n");
				writer.write("f" + " " + (8 + vertexCount) + " " + (7 + vertexCount) + " " + (3 + vertexCount)
						+ " " + (4 + vertexCount) + "\n");
				writer.write("f" + " " + (5 + vertexCount) + " " + (8 + vertexCount) + " " + (4 + vertexCount)
						+ " " + (1 + vertexCount) + "\n");
				writer.write("f" + " " + (6 + vertexCount) + " " + (7 + vertexCount) + " " + (8 + vertexCount)
						+ " " + (5 + vertexCount) + "\n");
				if (tile.getIsPointy()) {
					writer.write("f" + " " + (4 + vertexCount) + " " + (9 + vertexCount) + " " + (8 + vertexCount)
							+ "\n");
					writer.write("f" + " " + (8 + vertexCount) + " " + (9 + vertexCount) + " " + (5 + vertexCount)
							+ "\n");
					writer.write("f" + " " + (5 + vertexCount) + " " + (9 + vertexCount) + " " + (1 + vertexCount)
							+ "\n");
					writer.write("f" + " " + (1 + vertexCount) + " " + (9 + vertexCount) + " " + (4 + vertexCount)
							+ "\n");
					vertexCount += 9;
				} else {
					vertexCount += 8;
				}
			}

		}
		writer.close();
	}
	
	private static void initializeTemplates() {
		templateMapNames.add("bitcoinTemplate.terrainmap");
		templateMapNames.add("buildingCombo.terrainmap");
		templateMapNames.add("buildingPointy.terrainmap");
		templateMapNames.add("building.terrainmap");
		templateMapNames.add("castle.terrainmap");
		templateMapNames.add("city.terrainmap");
		templateMapNames.add("depression.terrainmap");
		templateMapNames.add("gate.terrainmap");
		templateMapNames.add("maze1.terrainmap");
		templateMapNames.add("Mountain.terrainmap");
		templateMapNames.add("pyramid.terrainmap");
		templateMapNames.add("road.terrainmap");
		templateMapNames.add("stadium.terrainmap");
		templateMapNames.add("surface.terrainmap");
		templateMapNames.add("Sus.terrainmap");
		templateMapNames.add("vill.terrainmap");
		templateMapNames.add("village2.terrainmap");
		templateMapNames.add("wave.terrainmap");
	}

	/**
	 * Returns a boolean with information on whether or not the TerrainMap has been saved yet
	 * 
	 * @return - openSave, a boolean with information on whether or not the TerrainMap has been saved yet
	 */
	public static boolean isOpenSave() {
		return openSave;
	}
	
	/**
	 * Returns a list of all the names of the TerrainMaps that have been saved
	 * 
	 * @return - a list of all the names of the TerrainMaps that have been saved
	 */
	public static List getSavedMapNames() {
		return TerrainMapIO.savedMapNames;
	}

	/**
	 * Sets the state of the boolean openSave
	 * 
	 * @param openSave - the given boolean to change the openSave variable to
	 */
	public static void setOpenSave(boolean openSave) {
		TerrainMapIO.openSave = openSave;
	}
}