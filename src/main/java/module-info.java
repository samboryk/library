module org.example.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    opens org.example.library to javafx.fxml;
    opens org.example.library.ui to javafx.fxml;
    opens org.example.library.model to org.hibernate.orm.core;

    exports org.example.library;
}
