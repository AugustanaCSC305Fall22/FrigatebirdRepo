module edu.augustana.csc305ProjectPreview {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.augustana.csc305ProjectPreview to javafx.fxml;
    exports edu.augustana.csc305ProjectPreview;
}
