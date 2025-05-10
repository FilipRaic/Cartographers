package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.thread.PlayerClientThread;
import hr.tvz.cartographers.shared.thread.PlayerServerThread;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static hr.tvz.cartographers.utils.GameStateUtil.startSaveGameStateThread;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerSynchronizationUtil {

    public static void startServerThreads(GameState gameState) {
        PlayerServerThread playerServerThread = new PlayerServerThread(gameState);
        Thread thread = new Thread(playerServerThread);
        thread.start();
    }

    public static void saveGameState(GameState gameState) {
        startSaveGameStateThread(gameState);

        PlayerClientThread playerClientThread = new PlayerClientThread(gameState);
        Thread thread = new Thread(playerClientThread);
        thread.start();
    }
}
