package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.exception.CustomException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hr.tvz.cartographers.utils.FileUtil.GAME_STATE_FILE_PATH;

public abstract class AbstractGameStateThread {

    private static Boolean fileAccessInProgress = false;

    public synchronized void saveGameMove(GameState currentGameState) {
        checkIfFileAccessInProgress();

        fileAccessInProgress = true;

        List<GameState> gameStates;
        if (currentGameState == null) {
            gameStates = new ArrayList<>();
        } else {
            gameStates = getAllGameStates();
            gameStates.add(currentGameState);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_STATE_FILE_PATH))) {
            oos.writeObject(gameStates);
        } catch (IOException e) {
            throw new CustomException("There was an error while saving the last game state to the file "
                    + GAME_STATE_FILE_PATH, e);
        }

        fileAccessInProgress = false;

        notifyAll();
    }

    public synchronized Optional<GameState> getGameState() {
        checkIfFileAccessInProgress();

        fileAccessInProgress = true;

        List<GameState> gameStates = getAllGameStates();

        fileAccessInProgress = false;

        notifyAll();

        return gameStates.isEmpty() ? Optional.empty() : Optional.of(gameStates.getLast());
    }

    private synchronized void checkIfFileAccessInProgress() {
        while (fileAccessInProgress.equals(Boolean.TRUE)) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(
                        "There was a problem with entering the waiting state of the game state thread!", e);
            }
        }
    }

    private List<GameState> getAllGameStates() {
        List<GameState> gameStates = new ArrayList<>();
        Path filePath = Path.of(GAME_STATE_FILE_PATH);

        if (Files.exists(filePath)) {
            try {
                if (Files.size(filePath) == 0)
                    return gameStates;

                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
                    Object obj = ois.readObject();
                    if (obj instanceof List) {
                        gameStates.addAll((List<GameState>) obj);
                    }
                } catch (EOFException e) {
                    return gameStates;
                } catch (IOException | ClassNotFoundException e) {
                    throw new CustomException("Error reading game state from file " + GAME_STATE_FILE_PATH, e);
                }
            } catch (IOException e) {
                throw new CustomException("Error checking file size for " + GAME_STATE_FILE_PATH, e);
            }
        }

        return gameStates;
    }
}