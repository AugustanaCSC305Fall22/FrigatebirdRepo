package frigatebird.ui;

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
    private void switchToTemplateMenu() throws IOException {
      App.setRoot("premadeTemplates");
    }
    
    @FXML
    private void exitAction() throws IOException {
        Platform.exit();
    }
}
