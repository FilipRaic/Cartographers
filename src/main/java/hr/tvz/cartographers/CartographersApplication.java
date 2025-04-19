package hr.tvz.cartographers;

import hr.tvz.cartographers.shared.enums.Player;
import hr.tvz.cartographers.shared.enums.ScreenConfiguration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CartographersApplication extends Application {

    private static final AtomicReference<Player> player = new AtomicReference<>();
    private static final AtomicReference<Stage> applicationStage = new AtomicReference<>();

    @Override
    public void start(Stage stage) throws IOException {
        applicationStage.set(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(CartographersApplication.class.getResource(ScreenConfiguration.MENU_SCREEN.getValue()));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(ScreenConfiguration.CARTOGRAPHERS.getValue());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            player.set(Player.valueOf(args[0]));
        } else {
            player.set(Player.SINGLE_PLAYER);
        }

        launch();
    }

    public static Player getPlayer() {
        return player.get();
    }

    public static Stage getApplicationStage() {
        return applicationStage.get();
    }
}
