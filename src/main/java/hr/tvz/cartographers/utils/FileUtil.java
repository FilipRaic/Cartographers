package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    public static final String GAME_STATE_FILE_PATH = "dat/gameState.ser";
    private static final String GAME_REPLAY_FILE_PATH = "dat/gameReplay.xml";

    public static void saveGameStatesToXmlFile(List<GameState> gameStates) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document replayGameDocument = documentBuilder.newDocument();

            Element rootElement = replayGameDocument.createElement("GameStates");
            replayGameDocument.appendChild(rootElement);

            for (GameState gameState : gameStates) {
                Element gameMoveElement = replayGameDocument.createElement("GameState");

                Element primaryGrid = replayGameDocument.createElement("PrimaryGrid");
                primaryGrid.setTextContent(Arrays.toString(gameState.getPrimaryGrid()));
                gameMoveElement.appendChild(primaryGrid);

                Element secondaryGrid = replayGameDocument.createElement("SecondaryGrid");
                secondaryGrid.setTextContent(Arrays.toString(gameState.getSecondaryGrid()));
                gameMoveElement.appendChild(secondaryGrid);

                rootElement.appendChild(gameMoveElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(replayGameDocument);
            StreamResult streamResult = new StreamResult(new File(GAME_REPLAY_FILE_PATH));
            transformer.transform(domSource, streamResult);
        } catch (Exception e) {
            throw new CustomException("There was an error while creating XML game replay file.", e);
        }
    }
}
