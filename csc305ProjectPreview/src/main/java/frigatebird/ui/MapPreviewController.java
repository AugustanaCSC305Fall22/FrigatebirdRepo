package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import frigatebird.terrainbuilder.Tile;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class MapPreviewController extends Application{
	
	Color color;
    
	@FXML
    private ColorPicker colorPicker = new ColorPicker();
    
    private TerrainMap map;
    private CompoundGroup group = new CompoundGroup();
    private PhongMaterial material = new PhongMaterial(Color.PURPLE);
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private Stage stage = new Stage();
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private static final int subSceneWidth = 700;
    private static final int subSceneHeight = 500;
    private static final int rowAndColSpanInpixels = 5;

    
		
	public void start(Stage stage) throws IOException {
		
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MapPreview.fxml"));
        AnchorPane fxmlPane = (AnchorPane) fxmlLoader.load();
        
        color = colorPicker.getValue();
		draw3DObject(color);
		Camera camera = new PerspectiveCamera();
		SubScene subscene = new SubScene(group, subSceneWidth, subSceneHeight, true, SceneAntialiasing.BALANCED);
		fxmlPane.getChildren().add(subscene);
		Scene scene = new Scene(fxmlPane, 900, 500, true);
		subscene.setFill(Color.SILVER);
		subscene.setCamera(camera);
		
		initMouseControl(group, subscene);
		initKeyboardControls(group, stage);
		stage.setScene(scene);
		stage.show();
}
	
    @FXML
    void changeColor(ActionEvent event) throws IOException {
    	System.out.println("hey");
    	color = colorPicker.getValue();
    	this.setMap(App.getMap());
    	group.getChildren().clear();
    	start(stage);
    }
    
    public Stage getStage() {
    	return this.stage;
    }
	
	private void draw3DObject(Color color) {
		int rowSpan = 0;
		int colSpan = 0;
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				Tile tile = map.getTileAt(r, c);
				int height = tile.getHeight();
				if(height == 0) {
					height = 1;
				}
				Box box = new Box(rowAndColSpanInpixels, height, rowAndColSpanInpixels);
				material.setDiffuseColor(color);
				box.setMaterial(material);
				box.translateXProperty().set(rowSpan);
				box.translateYProperty().set(height/-2);
				box.translateZProperty().set(colSpan);
				group.getChildren().add(box);
				if(tile.getIsPointy()) {
					makePointy(material, rowSpan, colSpan, height);
				}
				rowSpan += rowAndColSpanInpixels;
			}
			colSpan += rowAndColSpanInpixels;
			rowSpan = 0;
		}
		position3DObject();
	}
	
	private void position3DObject() {
		int mapSizeInPixels = rowAndColSpanInpixels * map.getNumRows();
		group.translateXProperty().set(subSceneWidth/2 - (mapSizeInPixels / 2));
		group.translateYProperty().set(subSceneHeight/1.7);
		group.translateZProperty().set(-400);
	}
	
	private void makePointy(PhongMaterial material, int rowSpan, int colSpan, int height) {
		double smallBoxWidthAndDepth = 5;
		double heightIncrement = 0.5;
		// approximate a pyramid using a stack of many shrinking boxes
		for(int i = 0; i < 50; i++) {
			Box smallBox = new Box(smallBoxWidthAndDepth, heightIncrement, smallBoxWidthAndDepth);
			smallBox.setMaterial(material);
			smallBox.translateXProperty().set(rowSpan);
			smallBox.translateYProperty().set((height/-2) - (height/2) - (heightIncrement/2));
			smallBox.translateZProperty().set(colSpan);
			group.getChildren().add(smallBox);
			smallBoxWidthAndDepth -= 0.1;
			heightIncrement += 0.5;
		}
	}
	
	private void initKeyboardControls(CompoundGroup group, Stage stage) {
		stage.addEventHandler (KeyEvent.KEY_PRESSED, e -> {
			switch(e.getCode()) {
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
	
	private void initMouseControl(CompoundGroup group, SubScene subscene){
		Rotate xRotate;
		Rotate yRotate;
		group.getTransforms().addAll(
				xRotate = new Rotate(0, Rotate.X_AXIS),
				yRotate = new Rotate(0, Rotate.Y_AXIS)
		);
		xRotate.angleProperty().bind(angleX);
		yRotate.angleProperty().bind(angleY);	
		
		subscene.setOnMousePressed(event -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();
			anchorAngleX = angleX.get();
			anchorAngleY = angleY.get();
		});
		
		subscene.setOnMouseDragged(event ->{
			angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
			angleY.set(anchorAngleY + anchorX - event.getSceneX());
		});
	}
	
	
	public void setMap(TerrainMap map) {
		this.map = map;
	}
	
	@FXML
    private void switchToMainMenu() throws IOException{
        App.setRoot("MainMenu");
    }
	
}


class CompoundGroup extends Group{
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