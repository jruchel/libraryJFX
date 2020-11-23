import utils.fxUtils.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController.setPrimaryStage(primaryStage);
        SceneController.startScene("login");
    }

    public static void main(String... args) {
        launch(args);
    }

}