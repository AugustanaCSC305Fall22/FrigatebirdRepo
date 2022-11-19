package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
    	int numRows = Integer.parseInt(rowTextField.getText());
    	int numColumns = Integer.parseInt(columnTextField.getText());
    	App.setMap(new TerrainMap(numRows, numColumns));
        App.setRoot("EditPage");
    }

    
}