package hr.tvz.cartographers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class CartographersApplication extends Application {

    private static final AtomicReference<Stage> applicationStage = new AtomicReference<>();

    @Override
    public void start(Stage stage) throws IOException {
        applicationStage.set(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource("screens/menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Cartographers");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getApplicationStage() {
        return applicationStage.get();
    }

    public static void main(String[] args) {
        launch();
    }
}
