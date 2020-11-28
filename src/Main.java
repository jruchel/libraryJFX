import utils.fxUtils.SceneController;
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController.setPrimaryStage(primaryStage);
        SceneController.setWidth(1200);
        SceneController.setHeight(675);
        SceneController.startScene("login");
    }

    public static void main(String... args) {
        launch(args);
    }

}

//TODO allow for refunds to be requested even if there previously was a refund request but it was rejected, ix a bug causing crashes when if was done
