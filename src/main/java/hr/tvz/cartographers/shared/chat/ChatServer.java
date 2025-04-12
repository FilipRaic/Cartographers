package hr.tvz.cartographers.shared.chat;

import hr.tvz.cartographers.shared.enums.NetworkConfiguration;
import hr.tvz.cartographers.shared.jndi.ConfigurationReader;
import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Slf4j
public class ChatServer {

    private static final int RANDOM_PORT_HINT = 0;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(ConfigurationReader.getIntegerValue(NetworkConfiguration.RMI_PORT));
            ChatRemoteService chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(chatRemoteService, RANDOM_PORT_HINT);
            registry.rebind(ChatRemoteService.REMOTE_OBJECT_NAME, skeleton);
        } catch (RemoteException e) {
            log.error("Error occurred when trying to initialize chat server: ", e);
        }
    }
}
