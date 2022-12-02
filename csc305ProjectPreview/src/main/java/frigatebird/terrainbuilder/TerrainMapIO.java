package frigatebird.terrainbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import com.google.gson.*;

import frigatebird.ui.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TerrainMapIO {

	private static boolean openSave = false;

	public static void terrainMapToJSON(TerrainMap map, File outputFile) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(outputFile);
		gson.toJson(map, writer);
		writer.close();
	}

	public static TerrainMap jsonToTerrainMap(File inputFile) throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(inputFile);
		TerrainMap map = gson.fromJson(reader, TerrainMap.class);
		reader.close();
		return map;
	}

	public static void save() throws IOException {
		File file = App.getCurrentFile();
		if (file != null) {
			terrainMapToJSON(App.getMap(), file);
		} else {
			saveAs();
		}
	}

	public static void saveAs() throws IOException {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)",
				"*.terrainmap");
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setInitialDirectory(App.getDirectory());
		Stage saveWindow = new Stage(StageStyle.TRANSPARENT);
		File file = fileChooser.showSaveDialog(saveWindow);
		if (file != null) {
			App.setCurrentFile(file);
			if(file.getParent() != null) {
				App.setDirectory(new File(file.getParent()));
			}
			terrainMapToJSON(App.getMap(), file);
			openSave = false;
		}
	}

	public static void confirmSave() throws IOException {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("You forgot to save!");
		confirm.setContentText("Would you like to save the changes made?");
		Optional<ButtonType> answer = confirm.showAndWait();

		if (answer.get() == ButtonType.OK) {
			saveAs();
		}
	}

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
			createObjFile(App.getMap(), file);
		}
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

	public static boolean isOpenSave() {
		return openSave;
	}

	public static void setOpenSave(boolean openSave) {
		TerrainMapIO.openSave = openSave;
	}
}