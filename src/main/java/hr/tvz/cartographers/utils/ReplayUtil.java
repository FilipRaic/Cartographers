package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static hr.tvz.cartographers.utils.FileUtil.GAME_STATE_FILE_PATH;
import static hr.tvz.cartographers.utils.FileUtil.saveGameStatesToXmlFile;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplayUtil {

    public static void saveCurrentGameStateToGameReplay() {
        Path currentGameStateFilePath = Path.of(GAME_STATE_FILE_PATH);

        if (Files.exists(currentGameStateFilePath)) {
            try (ObjectInputStream currentGameInputStream = new ObjectInputStream(new FileInputStream(currentGameStateFilePath.toFile()))) {
                if (Files.size(currentGameStateFilePath) == 0) {
                    return;
                }

                List<GameState> currentGameState = new ArrayList<>();
                Object obj = currentGameInputStream.readObject();
                if (obj instanceof List) {
                    currentGameState.addAll((List<GameState>) obj);
                    saveGameStatesToXmlFile(currentGameState);
                }
            } catch (EOFException e) {
                log.info("The game state file is empty");
            } catch (IOException | ClassNotFoundException e) {
                throw new CustomException("Error reading game state from file " + GAME_STATE_FILE_PATH, e);
            }
        }
    }
}
