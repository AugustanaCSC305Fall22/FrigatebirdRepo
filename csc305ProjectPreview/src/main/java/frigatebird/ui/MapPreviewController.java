package frigatebird.ui;

import java.io.IOException;

import frigatebird.terrainbuilder.TerrainMap;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class MapPreviewController extends Application{
	
    @FXML
    private BorderPane previewPane;
    private TerrainMap map;
    private CompoundGroup group = new CompoundGroup();
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private static final int windowWidth = 800;
    private static final int windowHeight = 500;
    private static final int rowAndColSpanInpixels = 5;

    
	
		
	public void start(Stage stage) {
		int rowSpan = 0;
		int colSpan = 0;
		for (int r = 0; r < map.getNumRows(); r++) {
			for (int c = 0; c < map.getNumColumns(); c++) {
				int height = map.getTileAt(r, c).getHeight();
				if(height == 0) {
					height = 1;
				}
				Box box = new Box(rowAndColSpanInpixels, height, rowAndColSpanInpixels);
				box.translateXProperty().set(rowSpan);
				box.translateYProperty().set(height/-2);
				box.translateZProperty().set(colSpan);
				group.getChildren().add(box);
				rowSpan += rowAndColSpanInpixels;
			}
			colSpan += rowAndColSpanInpixels;
			rowSpan = 0;
		}
		
		Camera camera = new PerspectiveCamera();
		
		Scene scene = new Scene(group, windowWidth, windowHeight, true);
		scene.setFill(Color.SILVER);
		scene.setCamera(camera);
		
		int mapSizeInPixels = rowAndColSpanInpixels * map.getNumRows();
		group.translateXProperty().set(windowWidth/2 - (mapSizeInPixels / 2));
		group.translateYProperty().set(windowHeight/1.6);
		group.translateZProperty().set(-400);
		
		initMouseContol(group, scene);
		//stage = (Stage) previewPane.getScene().getWindow();
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
		
		stage.setScene(scene);
		stage.show();
}
	
	private void initMouseContol(CompoundGroup group, Scene scene){
		Rotate xRotate;
		Rotate yRotate;
		group.getTransforms().addAll(
				xRotate = new Rotate(0, Rotate.X_AXIS),
				yRotate = new Rotate(0, Rotate.Y_AXIS)
		);
		xRotate.angleProperty().bind(angleX);
		yRotate.angleProperty().bind(angleY);	
		
		scene.setOnMousePressed(event -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();
			anchorAngleX = angleX.get();
			anchorAngleY = angleY.get();
		});
		
		scene.setOnMouseDragged(event ->{
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
	
	public static void main(String[] args) {
		launch(args);
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