package org.example.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.library.persistence.HibernateUtil;

import java.io.IOException;

public class LibraryApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(LibraryApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        scene.getStylesheets().add(LibraryApplication.class.getResource("app.css").toExternalForm());

        stage.setTitle("Бібліотека - Прокат книг");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        HibernateUtil.shutdown();
    }
}
