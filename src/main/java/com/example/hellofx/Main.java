package com.example.hellofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static StudentController studentC;
    public static ModuleController moduleC;
    public static RegisterModel registerM = new RegisterModel();


    @Override
    public void start(Stage stage) {
        openUI("student.fxml");
        openUI("module.fxml");

    }


    public static void main(String[] args) {
        launch();
    }

    public void openUI(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fileName));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle(fileName);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}