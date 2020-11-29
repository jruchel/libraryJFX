import utils.fxUtils.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController.setPrimaryStage(primaryStage);
        SceneController.setWidth(1200);
        SceneController.setHeight(675);
        SceneController.setTitle("Lrnfy.io");
        SceneController.startScene("login");
    }

    public static void main(String... args) {
        launch(args);
    }
}
