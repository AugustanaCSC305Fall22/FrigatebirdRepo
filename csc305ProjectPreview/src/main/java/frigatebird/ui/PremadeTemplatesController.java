package frigatebird.ui;

import java.io.File;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMapIO;
import javafx.fxml.FXML;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PremadeTemplatesController {

	@FXML
	private ImageView mazeTemp;

	@FXML
	private ImageView surfaceTemp;

	@FXML
	private ImageView citytemp;

	@FXML
	private ImageView castleTemp;

	@FXML
	private ImageView villageTemp;

	private String filePath;

	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}

	private String pathFinder(MouseEvent event) {
		if (event.getSource().equals(mazeTemp)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\maze1.terrainmap";
		}
		if (event.getSource().equals(castleTemp)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\castle.terrainmap";
		}
		if (event.getSource().equals(citytemp)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\city.terrainmap";
		}
		if (event.getSource().equals(surfaceTemp)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\surface.terrainmap";
		}*
		if (event.getSource().equals(villageTemp)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\village2.terrainmap";
		}
		return filePath;
	}

	@FXML
	void LoadToScreen(MouseEvent event) throws IOException {
		if (event.getButton().equals(MouseButton.PRIMARY)) {

			File file = new File(pathFinder(event));

			App.setMap(TerrainMapIO.jsonToTerrainMap(file));
			App.setCurrentFile(file);
			App.setRoot("EditPage");

		}

	}

}