module edu.augustana.csc305ProjectPreview {
	
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
	requires javafx.graphics;

    opens frigatebird.ui to javafx.fxml;
    opens frigatebird.terrainbuilder to com.google.gson;
    exports frigatebird.ui;
    
}
