package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class NewProjectMenuController {
	
	@FXML
	private TextField rowTextField;
	
	@FXML
	private TextField columnTextField;
	
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
    
    @FXML
    private void switchToEditPage() throws IOException {
    	
		try {
			int numRows = Integer.parseInt(rowTextField.getText());
			int numColumns = Integer.parseInt(columnTextField.getText());
			if (numRows > 99 || numColumns > 99) {
				new Alert(AlertType.ERROR, "Input must be less than 100").showAndWait();
			}
			else {
				App.setMap(new TerrainMap(numRows, numColumns));
		        App.setRoot("EditPage");
			}
		} catch (NumberFormatException e) {
			new Alert(AlertType.ERROR, "Input must be an integer").showAndWait();
		}
    }

    
}