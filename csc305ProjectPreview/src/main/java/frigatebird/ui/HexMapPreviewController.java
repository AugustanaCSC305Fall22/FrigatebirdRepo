package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class HexMapPreviewController {

	@FXML
	private ColorPicker colorPicker = new ColorPicker();
	@FXML
	private ComboBox<String> textureComboBox;

	private Color color;
	private TerrainMap map;
	private HexCompoundGroup group = new HexCompoundGroup();
	private double anchorX, anchorY;
	private double anchorAngleX = 0;
	private double anchorAngleY = 0;
	private static int rowIndexCount = 0;
	private Stage stage = new Stage();
	PhongMaterial material;

	private final DoubleProperty angleX = new SimpleDoubleProperty(0);
	private final DoubleProperty angleY = new SimpleDoubleProperty(0);
	private static final int subSceneWidth = 900;
	private static final int subSceneHeight = 470;
	private static final int TILE_WIDTH = 8;

	public HexMapPreviewController(TerrainMap map) {
		this.map = map;
	}

	public void start(Stage stage) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MapPreview.fxml"));
		fxmlLoader.setController(this);
		AnchorPane fxmlPane = (AnchorPane) fxmlLoader.load();

		color = colorPicker.getValue();
		textureComboBox.getItems().addAll("Brick", "Flame", "Glass", "Grass", "Iron", "Paper", "Rock", "Sand", "Space",
				"Water", "Wood");
		create3DObjects();

		Camera camera = new PerspectiveCamera();
		SubScene subscene = new SubScene(group, subSceneWidth, subSceneHeight, true, SceneAntialiasing.BALANCED);
		fxmlPane.getChildren().add(subscene);
		Scene scene = new Scene(fxmlPane, 900, 510, true);
		subscene.setFill(Color.SILVER);
		subscene.setCamera(camera);

		initMouseControl(group, subscene, stage);
		initKeyboardControls(group, stage);
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	void changeColor(ActionEvent event) throws IOException {
		color = colorPicker.getValue();
		group.getChildren().clear();
		create3DObjects();
	}

	@FXML
	void changeTexture() {
		applyTexture();
	}

	private void applyTexture() {
		color = Color.WHITE;
		group.getChildren().clear();
		create3DObjects();
		for (int i = 0; i < group.getChildren().size(); i++) {
			Box box = (Box) group.getChildren().get(i);
			String texture = textureComboBox.getValue();
			String path = "Textures/" + texture + ".jpg";
			material.setDiffuseMap(new Image(getClass().getResourceAsStream(path)));
			box.setMaterial(material);
		}
	}

	private void create3DObjects() {
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				if (height == 0) {
					height = 1;
				}

				HexCompoundGroup hexagon = createHexGroup(tile);
				group.getChildren().add(hexagon);
				

				if (tile.getIsPointy()) {
					makePointy(material, tile);
				}
			}
		}
		position3DObject();
	}
	
	private HexCompoundGroup createHexGroup(Tile tile) {
		Transform transform60 = new Rotate(60, new Point3D(0, 1, 0));
		Transform transform120 = new Rotate(120, new Point3D(0, 1, 0));
		Box box1 = createRectangleBox(tile);
		Box box2 = createRectangleBox(tile);
		box2.getTransforms().add(transform60);
		Box box3 = createRectangleBox(tile);
		box3.getTransforms().add(transform120);
		HexCompoundGroup hexGroup = new HexCompoundGroup();
		hexGroup.getChildren().add(box1);
		hexGroup.getChildren().add(box2);
		hexGroup.getChildren().add(box3);
		return hexGroup; 
		
	}

	private Box createRectangleBox(Tile tile) {
		double height = tile.getHeight() * TILE_WIDTH;
		Box box = new Box(TILE_WIDTH, height, TILE_WIDTH / Math.sqrt(3.0));
		material = new PhongMaterial(color);
		box.translateXProperty().set(tile.getCenterX(TILE_WIDTH));
		box.translateYProperty().set(height / -2);
		box.translateZProperty().set(tile.getCenterY(TILE_WIDTH));
		box.setMaterial(material);
		return box;
	}
	
	private void position3DObject() {
		int mapSizeInPixels = TILE_WIDTH * map.getNumRows();
		group.translateXProperty().set(subSceneWidth / 2 - (mapSizeInPixels / 2));
		group.translateYProperty().set(subSceneHeight / 1.7);
		group.translateZProperty().set(-400);
	}

	private void makePointy(PhongMaterial material, Tile tile) {
		double smallBoxWidthAndDepth = 5;
		double heightIncrement = 0.5 * 8;
		double height = tile.getHeight() * TILE_WIDTH;
		// approximate a pyramid using a stack of many shrinking boxes
		for (int i = 0; i < 50; i++) {
			Box smallHex = new Box(TILE_WIDTH, height, TILE_WIDTH / Math.sqrt(3.0));
			smallHex.translateXProperty().set(tile.getCenterX(TILE_WIDTH)); 
			smallHex.translateYProperty().set(-height - (heightIncrement / 2));
			smallHex.translateZProperty().set(tile.getCenterY(TILE_WIDTH));
			group.getChildren().add(smallHex);
			smallBoxWidthAndDepth -= 0.1;
			heightIncrement += heightIncrement;
		}
	}

	private void initKeyboardControls(HexCompoundGroup group, Stage stage) {
		stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			switch (e.getCode()) {
			case W:
				group.translateZProperty().set(group.getTranslateZ() + 100);
				break;
			case S:
				group.translateZProperty().set(group.getTranslateZ() - 100);
				break;
			case Q:
				group.rotateByX(10);
				break;
			case E:
				group.rotateByX(-10);
				break;
			case A:
				group.rotateByY(10);
				break;
			case D:
				group.rotateByY(-10);
				break;
			}
		});
	}

	private void initMouseControl(HexCompoundGroup group, SubScene subscene, Stage stage) {
		Rotate xRotate;
		Rotate yRotate;
		group.getTransforms().addAll(xRotate = new Rotate(0, Rotate.X_AXIS), yRotate = new Rotate(0, Rotate.Y_AXIS));
		xRotate.angleProperty().bind(angleX);
		yRotate.angleProperty().bind(angleY);

		subscene.setOnMousePressed(event -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();
			anchorAngleX = angleX.get();
			anchorAngleY = angleY.get();
		});

		subscene.setOnMouseDragged(event -> {
			angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
			angleY.set(anchorAngleY + anchorX - event.getSceneX());
		});

		stage.addEventHandler(ScrollEvent.SCROLL, e -> {
			double deltaY = e.getDeltaY();
			group.translateZProperty().set(group.getTranslateZ() + deltaY);
		});
	}

	@FXML
	private void switchToMainMenu() throws IOException {
		App.setRoot("MainMenu");
	}

	public Stage getStage() {
		return this.stage;
	}

}

class HexCompoundGroup extends Group {
	Rotate rotationDegree;
	Transform transform = new Rotate();

	void rotateByX(int angle) {
		rotationDegree = new Rotate(angle, Rotate.X_AXIS);
		transform = transform.createConcatenation(rotationDegree);
		this.getTransforms().clear();
		this.getTransforms().add(transform);
	}

	void rotateByY(int angle) {
		rotationDegree = new Rotate(angle, Rotate.Y_AXIS);
		transform = transform.createConcatenation(rotationDegree);
		this.getTransforms().clear();
		this.getTransforms().add(transform);
	}

}