module edu.augustana.csc305ProjectPreview {
    requires javafx.controls;
    requires javafx.fxml;

    opens frigatebird.terrainbuilder to javafx.fxml;
    exports frigatebird.terrainbuilder;
}
