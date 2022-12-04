package frigatebird.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static TerrainMap map;
    private static File currentFile = null;
    private static File directory;

	private static String view = "Top Down View";

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainMenu"), 900, 594);
        stage.setTitle("Frigatebird 3D Editor");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

	public static TerrainMap getMap() {
		return map;
	}

	public static void setMap(TerrainMap map) {
		App.map = map;
	}
	
	public static File getCurrentFile() {
		return currentFile;
	}
	
	public static void setCurrentFile(File file) {
		currentFile = file;
	}

	public static String getView() {
		return view;
	}

	public static void setView(String view) {
		App.view = view;
	}
	
	public static File getDirectory() {
		return directory;
	}

	public static void setDirectory(File directory) {
		App.directory = directory;
	}

}