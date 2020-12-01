import javafx.scene.image.Image;
import utils.fxUtils.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image("file:src/resources/images/appIcon.png"));
        primaryStage.setTitle("Library");
        SceneController.setPrimaryStage(primaryStage);
        SceneController.setWidth(1200);
        SceneController.setHeight(675);
        SceneController.startScene("login");
    }

    public static void main(String... args) {
        launch(args);
    }
}