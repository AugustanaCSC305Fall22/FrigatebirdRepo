package frigatebird.terrainbuilder;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class ProjectMenuController {

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }

    @FXML
    private void switchToEditPage() throws IOException {
        App.setRoot("EditPage");
    }
    

}
