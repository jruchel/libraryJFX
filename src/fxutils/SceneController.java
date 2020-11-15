package fxutils;

import controllers.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class SceneController {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static URL constructURL(String file) throws MalformedURLException {
        file += ".fxml";
        URL url = new File("src/resources/" + file).toURI().toURL();
        return url;
    }

    private static Scene getScene(String name) throws IOException {
        URL url = constructURL(name);
        Parent root = FXMLLoader.load(url);
        return new Scene(root, 600, 400);
    }

    private static FXMLLoader getLoader(String file) throws MalformedURLException {
        return new FXMLLoader(constructURL(file));
    }

    private static Scene getScene(String name, Scene scene) throws IOException {
        URL url = constructURL(name);
        Parent root = FXMLLoader.load(url);
        scene.setRoot(root);
        return scene;
    }

    public static void startScene(String file, Map<String, Object> parameters) throws IOException {
        FXMLLoader loader = getLoader(file);
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setParameters(parameters);
        controller.initializeManually();
        Scene scene = new Scene(root, 600, 400);
        startScene(scene);
    }

    public static void startScene(Scene scene) {
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void startScene(String file) throws IOException {
        primaryStage.setResizable(false);
        primaryStage.setScene(getScene(file));
        primaryStage.show();
    }

    public static void startScene(String file, Scene parameters) throws IOException {
        primaryStage.setResizable(false);
        primaryStage.setScene(getScene(file, parameters));
        primaryStage.show();
    }

}
