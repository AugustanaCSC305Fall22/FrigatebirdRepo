package frigatebird.ui;

import java.io.File;


import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMapIO;
import javafx.fxml.FXML;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Controls the menu and ui to select from premade TerrainMap templates
 */
public class PremadeTemplatesController {

	@FXML
    private ImageView chicago;

    @FXML
    private ImageView stadium;

    @FXML
    private ImageView winterFell;

    @FXML
    private ImageView street;

    @FXML
    private ImageView maze;

    @FXML
    private ImageView bitCoin;

    @FXML
    private ImageView building;

    @FXML
    private ImageView road;

    @FXML
    private ImageView village;

	private String filePath;

	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}

	private String pathFinder(MouseEvent event) {
		if (event.getSource().equals(maze)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\maze1.terrainmap";
		}
		if (event.getSource().equals(winterFell)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\castle.terrainmap";
		}
		if (event.getSource().equals(chicago)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\city.terrainmap";
		}
		if (event.getSource().equals(bitCoin)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\bitcoinTemplate.terrainmap";
		}
		if (event.getSource().equals(village)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\vill.terrainmap";
		}
		if (event.getSource().equals(street)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\village2.terrainmap";
		}
		if (event.getSource().equals(building)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\buildingCombo.terrainmap";
		}
		if (event.getSource().equals(road)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\road.terrainmap";
		}
		if (event.getSource().equals(stadium)) {
			filePath = "src\\main\\resources\\frigatebird\\Templates\\stadium.terrainmap";
		}
		return filePath;
	}

	@FXML
	private void LoadToScreen(MouseEvent event) throws IOException {
		if (event.getButton().equals(MouseButton.PRIMARY)) {

			File file = new File(pathFinder(event));

			App.setMap(TerrainMapIO.jsonToTerrainMap(file));
			App.setCurrentFile(file);
			App.setRoot("EditPage");

		}

	}

}