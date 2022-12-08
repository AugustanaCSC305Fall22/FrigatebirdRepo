module edu.augustana.FrigatebirdProject {
	
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
	requires javafx.graphics;
	requires de.jensd.fx.glyphs.commons;
	requires de.jensd.fx.glyphs.materialdesignicons;
	requires de.jensd.fx.glyphs.fontawesome;
	requires javafx.base;
	requires com.jfoenix;
	
	
	
    opens frigatebird.ui to javafx.fxml;
    opens frigatebird.terrainbuilder to com.google.gson;
    exports frigatebird.ui;
    exports frigatebird.terrainbuilder;
}
