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
    private static Scene fileWindow;
    private static TerrainMap map = new TerrainMap();
    private static File currentFile = null;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainMenu"), 900, 600);
        BorderPane b = new BorderPane();
        fileWindow = new Scene(b, 1, 1);
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
	
	public static File saveFile() {
		FileChooser fileChooser = new FileChooser();
    	FileChooser.ExtensionFilter filter = 
                new FileChooser.ExtensionFilter("Terrain map (*.terrainmap)", "*.terrainmap");
        fileChooser.getExtensionFilters().add(filter);
		Stage saveWindow = new Stage(StageStyle.TRANSPARENT);
		saveWindow.setScene(fileWindow);
		saveWindow.show();
    	File file = fileChooser.showSaveDialog(saveWindow);
    	saveWindow.close();
    	//File rename = new File(file.getName() + ".json");
    	//file.renameTo(rename);
    	return file;
	}
    
    

}