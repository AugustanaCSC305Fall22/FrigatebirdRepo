package edu.augustana.csc305ProjectPreview;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToProjectMenu() throws IOException {
        App.setRoot("projectMenu");
    }
    
    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("mainMenu");
    }
    
    @FXML
    private void switchToEditPage() throws IOException {
        App.setRoot("editPage");
    }
    
    @FXML
    private void exitProgram() throws IOException {
        Platform.exit();
    }
}
