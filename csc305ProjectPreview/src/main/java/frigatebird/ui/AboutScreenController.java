package frigatebird.ui;

import java.io.IOException;
import javafx.fxml.FXML;

public class AboutScreenController {
	
	@FXML
    private void switchToEditPage() throws IOException {
        App.setRoot("EditPage");
    }
}