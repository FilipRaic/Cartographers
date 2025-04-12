package hr.tvz.cartographers.shared.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatRemoteService extends Remote {
    String REMOTE_OBJECT_NAME = "hr.tvz.cartographers.shared.chat";

    void sendChatMessage(String message) throws RemoteException;

    List<String> getAllChatMessages() throws RemoteException;
}