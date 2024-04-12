package com.example.ba_transaktionen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("SimGui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Simulation des Transaktionsverhaltens einer Währung");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}