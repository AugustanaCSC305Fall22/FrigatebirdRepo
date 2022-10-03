module edu.augie.Frigatebirdproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.augie.Frigatebirdproject to javafx.fxml;
    exports edu.augie.Frigatebirdproject;
}
