package frigatebird.ui;

import java.io.IOException;
import javafx.fxml.FXML;

public class NewProjectMenuController {

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
    
    @FXML
    private void switchToEditPage() throws IOException {
        App.setRoot("EditPage");
    }

    
}