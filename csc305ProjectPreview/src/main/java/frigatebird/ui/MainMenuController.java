package frigatebird.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.jfoenix.controls.JFXListView;

import frigatebird.terrainbuilder.TerrainMapIO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class MainMenuController {

	@FXML
	private JFXListView<String> listView;
    @FXML
    private Label noProjectsLabel;
	private List<String> savedMapNames = new ArrayList<>();
	private File dir;
	private String[] directoryChildren;

	@FXML
	private void initialize() {
		dir = new File("src\\main\\resources\\frigatebird\\SavedMaps");
		if (dir.isDirectory()) {
			directoryChildren = dir.list();
			
			for(int i = 0; i < directoryChildren.length; i++) {
				savedMapNames.add(directoryChildren[i]);
			}
			listView.getItems().addAll(savedMapNames);
			if(listView.getItems().size() != 0) {
			    noProjectsLabel.setVisible(false);
			}
		}
	}
	
    @FXML
    private void editMapAction() throws JsonSyntaxException, JsonIOException, IOException {
    	String mapToEdit = listView.getSelectionModel().getSelectedItem();
    	if (mapToEdit != null) {
    		File fileToOpen = new File("src\\main\\resources\\frigatebird\\SavedMaps\\" + mapToEdit);
			App.setMap(TerrainMapIO.jsonToTerrainMap(fileToOpen));
			App.setCurrentFile(fileToOpen);
			App.setRoot("EditPage");
    	} else {
    		new Alert(AlertType.WARNING, "Select a map first!").show();
    	}
    }
    
    @FXML
    private void deleteMapAction(ActionEvent event) throws IOException {
		String mapToDelete = listView.getSelectionModel().getSelectedItem();
		int mapNameIndex = listView.getSelectionModel().getSelectedIndex();
		
		if (mapToDelete != null) {
			listView.getItems().remove(mapToDelete);	
			savedMapNames.remove(mapNameIndex);
			deleteSavedFile(mapToDelete);
		}
	}
    
	/**
	 * Removes a saved map file from the directory
	 * 
	 * @param mapName - name of the map file to delete from the save directory
	 * @throws IOException - general exception for failed or interrupted I/O operations
	 */
	public void deleteSavedFile(String mapName) throws IOException {
		Path path = Paths.get("src\\main\\resources\\frigatebird\\SavedMaps\\" + mapName);
		if (dir.isDirectory()) {
			for (int i = 0; i < directoryChildren.length; i++) {
				if (directoryChildren[i].equals(mapName)) {
					Files.delete(path);
				}
			}
		}
	}

	@FXML
	private void switchToProjectMenu() throws IOException {
		App.setRoot("ProjectMenu");
	}

	@FXML
	private void switchToNewProjectMenu() throws IOException {
		App.setRoot("NewProjectMenu");
	}

	@FXML
	private void switchToTemplateMenu() throws IOException {
		App.setRoot("premadeTemplates");
	}

	@FXML
	private void exitAction() throws IOException {
		Platform.exit();
	}
}
