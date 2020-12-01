package utils.fxUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SceneController {

    private static Stage primaryStage;
    private static int width, height;
    private static Map<String, Scene> scenes;
    private static String resourceDirectory = "src/resources/";

    static {
        scenes = new HashMap<>();
        width = 600;
        height = 600;
    }

    public static void setResourceDirectory(String resourceDirectory) {
        SceneController.resourceDirectory = resourceDirectory;
    }

    public static void setTitle(String title) {
        primaryStage.setTitle(title);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setHeight(int height) {
        SceneController.height = height;
    }

    public static void setWidth(int width) {
        SceneController.width = width;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    private static URL constructURL(String file) throws MalformedURLException {
        file += ".fxml";
        URL url = new File(resourceDirectory + file).toURI().toURL();
        return url;
    }

    private static Scene getScene(String name) throws IOException {
        URL url = constructURL(name);
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root, width, height);
        return scene;
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
