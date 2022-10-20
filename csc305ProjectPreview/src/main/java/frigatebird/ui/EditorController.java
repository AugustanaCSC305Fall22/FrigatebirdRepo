package frigatebird.ui;

import java.io.IOException;
import javafx.fxml.FXML;

public class EditorController {

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
}