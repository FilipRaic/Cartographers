package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.enums.Season;
import hr.tvz.cartographers.enums.TerrainType;
import hr.tvz.cartographers.models.ExploreCard;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

import static hr.tvz.cartographers.models.ExploreCard.getExploreCardsDeck;
import static hr.tvz.cartographers.utils.AmbushUtil.handleAmbush;
import static hr.tvz.cartographers.utils.SeasonUtil.endSeason;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CardUtil {

    public static void drawNextCard() {
        Season season = GameUtil.getCurrentSeason();
        List<ExploreCard> exploreDeck = GameUtil.getExploreDeck();
        int time = GameUtil.getCurrentTime();

        if (season.equals(Season.END))
            return;

        if (exploreDeck.isEmpty()) {
            endSeason();
            return;
        }

        ExploreCard card = exploreDeck.removeFirst();
        GameUtil.setCurrentCard(card);
        if (card.isRuins()) {
            drawNextCard();
        } else if (card.isAmbush()) {
            handleAmbush();
        } else {
            updateCardDisplay();
            GameUtil.setCurrentTime(time + card.getTime());

            if (time >= season.getThreshold()) {
                endSeason();
            }
        }
    }

    public static void updateCardDisplay() {
        VBox cardDisplay = GameUtil.getCardDisplay();
        ExploreCard currentCard = GameUtil.getCurrentCard();

        cardDisplay.getChildren().clear();
        if (currentCard != null) {
            cardDisplay.getChildren().add(new Label(currentCard.getName() + " (Time: " + currentCard.getTime() + ")"));
            for (List<int[]> shape : currentCard.getShapes()) {
                for (TerrainType terrain : currentCard.getTerrains()) {
                    Button option = new Button(terrain.name());
                    option.setOnAction(_ -> {
                        GameUtil.setCurrentShape(shape);
                        GameUtil.setCurrentTerrain(terrain);
                    });

                    cardDisplay.getChildren().add(option);
                }
            }
        }
    }

    public static void initializeExploreDeck() {
        List<List<int[]>> allUniqueShapes = GameUtil.getAllUniqueShapes();
        List<ExploreCard> exploreDeck = getExploreCardsDeck(allUniqueShapes);

        Collections.shuffle(exploreDeck);
        GameUtil.setExploreDeck(exploreDeck);
    }
}
