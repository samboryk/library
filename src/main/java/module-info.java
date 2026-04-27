module org.example.library {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.library to javafx.fxml;
    exports org.example.library;
}