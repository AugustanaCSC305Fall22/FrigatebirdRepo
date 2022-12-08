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
 * The main App of the 3D TerrainMap editor, launches the program itself
 */
public class App extends Application {

    private static Scene scene;
    private static TerrainMap map;
    private static File currentFile = null;
    private static File directory;

	private static String view = "Top Down View";

    /**
     * Starts the application by displaying the scene the user sees
     * 
     * @param stage - the stage to be initialized to display the other scenes on
     * @throws IOException - general exception for failed or interrupted I/O operations
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainMenu"), 900, 594);
        stage.setTitle("Frigatebird 3D Editor");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the root of the scene to display a given fxml file
     * 
     * @param fxml - a string corresponding to an fxml file
     * @throws IOException - general exception for failed or interrupted I/O operations
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Launches the program
     * 
     * @param args - given variable from the default of this method
     */
    public static void main(String[] args) {
        launch();
    }

	/**
	 * Returns the current TerrainMap
	 * 
	 * @return - the current TerrainMap object
	 */
	public static TerrainMap getMap() {
		return map;
	}

	/**
	 * Sets the TerrainMap variable of this class to a given TerrainMap object
	 * 
	 * @param map - a given TerrainMap object
	 */
	public static void setMap(TerrainMap map) {
		App.map = map;
	}
	
	/**
	 * Returns the currentFile variable of the App class
	 * 
	 * @return - the currentFile variable
	 */
	public static File getCurrentFile() {
		return currentFile;
	}
	
	/**
	 * Sets the currentFile variable in the App class data fields to a given file
	 * 
	 * @param file - a given file object
	 */
	public static void setCurrentFile(File file) {
		currentFile = file;
	}

	/**
	 * Returns the view variable from the App class's data fields
	 * 
	 * @return - the view variable from the App class's data fields
	 */
	public static String getView() {
		return view;
	}

	/**
	 * Sets the view variable in the App class's data fields
	 * 
	 * @param view - a string representing a view used to display the TerrainMap object
	 */
	public static void setView(String view) {
		App.view = view;
	}
	
	/**
	 * Returns the file directory variable from the App class's data fields
	 * 
	 * @return - the current file directory from the App class's data fields
	 */
	public static File getDirectory() {
		return directory;
	}

	/**
	 * Sets the file directory variable in the App class's data fields
	 * 
	 * @param directory - a file variable
	 */
	public static void setDirectory(File directory) {
		App.directory = directory;
	}

}