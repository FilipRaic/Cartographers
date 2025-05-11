package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.enums.NetworkConfiguration;
import hr.tvz.cartographers.shared.jndi.ConfigurationReader;
import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public final class PlayerServerThread implements Runnable {

    @Override
    public void run() {
        acceptRequestFromOtherPlayer();
    }

    private void acceptRequestFromOtherPlayer() {
        Optional<NetworkConfiguration> playerServerPortConfig = NetworkConfiguration.getPlayerServerPortConfiguration();

        if (playerServerPortConfig.isEmpty())
            return;

        try (ServerSocket serverSocket = new ServerSocket(ConfigurationReader.getIntegerValue(playerServerPortConfig.get()))) {
            log.info("Player server listening on port: {}", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Player client connected from port: {}", clientSocket.getPort());

                if (clientSocket.isClosed())
                    break;

                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (BindException _) {
            log.info("Player already listening to port");
        } catch (IOException e) {
            log.error("Player server listening error", e);
        }
    }

    private void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
            GameState receivedGameState = (GameState) ois.readObject();
            refreshGameState(receivedGameState);

            log.info("Current game state received!");
            oos.writeObject(Boolean.TRUE);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Process serializable client error", e);
        }
    }

    private void refreshGameState(GameState receivedGameState) {
        Platform.runLater(new RefreshGameStateThread(receivedGameState));
    }
}
