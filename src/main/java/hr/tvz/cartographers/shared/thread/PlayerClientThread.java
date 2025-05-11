package hr.tvz.cartographers.shared.thread;

import hr.tvz.cartographers.models.GameState;
import hr.tvz.cartographers.shared.enums.NetworkConfiguration;
import hr.tvz.cartographers.shared.jndi.ConfigurationReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public final class PlayerClientThread implements Runnable {

    private GameState gameState;

    @Override
    public void run() {
        sendRequest();
    }

    private void sendRequest() {
        Optional<NetworkConfiguration> playerClientPortConfig = NetworkConfiguration.getPlayerClientPortConfiguration();

        if (playerClientPortConfig.isEmpty())
            return;

        try (Socket clientSocket = new Socket(
                ConfigurationReader.getStringValue(NetworkConfiguration.HOSTNAME),
                ConfigurationReader.getIntegerValue(playerClientPortConfig.get()))) {
            log.info("Client is connecting to {}:{}", clientSocket.getInetAddress(), clientSocket.getPort());

            sendSerializableRequest(clientSocket);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error sending request", e);
        }
    }

    private void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        log.info("Game state received confirmation: {}", ois.readObject());
    }
}
