package hr.tvz.cartographers.shared.chat;

import lombok.NoArgsConstructor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ChatRemoteServiceImpl implements ChatRemoteService {

    private final List<String> chatMessages = new ArrayList<>();

    @Override
    public void sendChatMessage(String message) throws RemoteException {
        chatMessages.add(message);
    }

    @Override
    public List<String> getAllChatMessages() throws RemoteException {
        return chatMessages;
    }
}
