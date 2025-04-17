package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameMove;
import hr.tvz.cartographers.shared.exception.CustomException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGameMoveThread {

    private static Boolean fileAccessInProgress = false;
    private static final String GAME_MOVES_FILE_PATH = "dat/currentGameState.ser";

    public synchronized void saveGameMove(GameMove theLastGameMove) {
        checkIfFileAccessInProgress();

        fileAccessInProgress = true;

        List<GameMove> gameMoves = getAllGamesMoves();
        gameMoves.add(theLastGameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_MOVES_FILE_PATH))) {
            oos.writeObject(gameMoves);
        } catch (IOException e) {
            throw new CustomException("There was an error while saving the last game move to the file "
                    + GAME_MOVES_FILE_PATH, e);
        }

        fileAccessInProgress = false;

        notifyAll();
    }

    public synchronized Optional<GameMove> getGameMove() {
        checkIfFileAccessInProgress();

        fileAccessInProgress = true;

        List<GameMove> gameMoves = getAllGamesMoves();

        fileAccessInProgress = false;

        notifyAll();

        return gameMoves.isEmpty() ? Optional.empty() : Optional.of(gameMoves.getLast());
    }

    private synchronized void checkIfFileAccessInProgress() {
        while (fileAccessInProgress.equals(Boolean.TRUE)) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(
                        "There was a problem with entering the waiting state of the game move thread!", e);
            }
        }
    }

    private List<GameMove> getAllGamesMoves() {
        List<GameMove> gameMoveList = new ArrayList<>();
        Path filePath = Path.of(GAME_MOVES_FILE_PATH);

        if (Files.exists(filePath)) {
            try {
                if (Files.size(filePath) == 0) {
                    return gameMoveList;
                }
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
                    Object obj = ois.readObject();
                    if (obj instanceof List) {
                        gameMoveList.addAll((List<GameMove>) obj);
                    }
                } catch (EOFException e) {
                    return gameMoveList;
                } catch (IOException | ClassNotFoundException e) {
                    throw new CustomException("Error reading game moves from file " + GAME_MOVES_FILE_PATH, e);
                }
            } catch (IOException e) {
                throw new CustomException("Error checking file size for " + GAME_MOVES_FILE_PATH, e);
            }
        }

        return gameMoveList;
    }
}