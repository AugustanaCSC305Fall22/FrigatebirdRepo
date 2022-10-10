package frigatebird.terrainbuilder;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuController {
	
    @FXML
    private void switchToProjectMenu() throws IOException {
        App.setRoot("ProjectMenu");
    }
    
    @FXML
    private void switchToNewProjectMenu() throws IOException {
        App.setRoot("NewProjectMenu");
    }
    
    @FXML
    private void exitProgram() throws IOException {
        Platform.exit();
    }
}
