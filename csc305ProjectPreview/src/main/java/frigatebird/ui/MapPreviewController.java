package frigatebird.ui;

import java.io.IOException;
import javafx.fxml.FXML;

public class MapPreviewController {
	
	@FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("MainMenu");
    }
	
}
