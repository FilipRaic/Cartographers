package hr.tvz.cartographers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CartographersApplication extends Application {

    public static Stage applicationStage;

    @Override
    public void start(Stage stage) throws IOException {
        applicationStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource("screens/menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cartographers");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
