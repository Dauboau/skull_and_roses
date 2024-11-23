module skull_and_roses {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens skull_and_roses to javafx.fxml;
    exports skull_and_roses;
}
