package fxutils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SceneController {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static Scene getScene(String name) throws IOException {
        name += ".fxml";
        URL url = new File("src/resources/" + name).toURI().toURL();
        Parent root = FXMLLoader.load(url);
        return new Scene(root, 500, 500);
    }

    private static Scene getScene(String name, Scene scene) throws IOException {
        name += ".fxml";
        URL url = new File("src/resources/" + name).toURI().toURL();
        Parent root = FXMLLoader.load(url);
        scene.setRoot(root);
        return scene;
    }

    public static void startScene(String file) throws IOException {
        primaryStage.setScene(getScene(file));
        primaryStage.show();
    }

    public static void startScene(String file, Scene parameters) throws IOException {
        primaryStage.setScene(getScene(file, parameters));
        primaryStage.show();
    }

}
