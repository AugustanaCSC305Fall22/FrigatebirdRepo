package frigatebird.ui;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import frigatebird.terrainbuilder.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class NewProjectMenuController {
	
	@FXML
	private TextField nameTextArea;
	
	@FXML
	private TextField rowTextField;
	
	@FXML
	private TextField columnTextField;
	
	@FXML
	private JFXButton createButton;
	
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
    
    @FXML
    private void switchToEditPage() throws IOException {
    	
		try {
			String mapName = nameTextArea.getText();
			int numRows = Integer.parseInt(rowTextField.getText());
			int numColumns = Integer.parseInt(columnTextField.getText());
			if (numRows > 99 || numColumns > 99) {
				new Alert(AlertType.ERROR, "Input must be less than 100").showAndWait();
			}
			else if(mapName.equals(" ") || mapName == null || mapName.equals("")) {
				new Alert(AlertType.ERROR, "Please enter a name for your project.").showAndWait();
			}
			else {
				App.setMap(new TerrainMap(mapName, numRows, numColumns, false));
		        App.setRoot("EditPage");
			}
		} catch (NumberFormatException e) {
			new Alert(AlertType.ERROR, "Input must be an integer").showAndWait();
		}
    }

    
}