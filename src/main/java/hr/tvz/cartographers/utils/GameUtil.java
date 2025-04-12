package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.shared.enums.ScreenConfiguration;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUtil {

    public static void setGameBackgroundImage(Parent root, ScreenConfiguration screenConfiguration) {
        if (root instanceof Region rootRegion) {
            URL imagePath = CartographersApplication.class.getResource(screenConfiguration.getValue());

            if (imagePath != null)
                rootRegion.setStyle("-fx-background-image: url('" + imagePath.toExternalForm() + "'); ");
            else
                log.error("Image resource not found");
        } else {
            log.warn("Root node is not a Region, cannot set background image");
        }
    }
}
