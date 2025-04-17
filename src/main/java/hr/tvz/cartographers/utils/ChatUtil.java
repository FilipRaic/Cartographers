package hr.tvz.cartographers.utils;

import hr.tvz.cartographers.CartographersApplication;
import hr.tvz.cartographers.shared.chat.ChatRemoteService;
import hr.tvz.cartographers.shared.enums.NetworkConfiguration;
import hr.tvz.cartographers.shared.exception.CustomException;
import hr.tvz.cartographers.shared.jndi.ConfigurationReader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatUtil {

    public static void sendChatMessage(String chatMessage) {
        try {
            if (chatMessage == null || chatMessage.isBlank())
                return;

            ChatRemoteService chatRemoteService = getChatRemoteService();

            chatRemoteService.sendChatMessage(CartographersApplication.getPlayer().getLabel() + ": " + chatMessage);
        } catch (RemoteException e) {
            throw new CustomException("Error while sending a chat message: ", e);
        }
    }

    public static Timeline getChatTimeline(TextArea chatTextArea) {
        Timeline chatMessagesTimeline = new Timeline(new KeyFrame(Duration.millis(500), (ActionEvent _) -> {
            try {
                ChatRemoteService chatRemoteService = getChatRemoteService();

                List<String> chatMessages = chatRemoteService.getAllChatMessages();
                String chatMessagesString = String.join("\n", chatMessages);
                chatTextArea.setText(chatMessagesString);
            } catch (RemoteException e) {
                throw new CustomException("An error occurred while creating the timeline for chat: ", e);
            }
        }), new KeyFrame(Duration.seconds(0.5)));

        chatMessagesTimeline.setCycleCount(Animation.INDEFINITE);
        return chatMessagesTimeline;
    }

    private static ChatRemoteService getChatRemoteService() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(
                    ConfigurationReader.getStringValue(NetworkConfiguration.HOSTNAME),
                    ConfigurationReader.getIntegerValue(NetworkConfiguration.RMI_PORT));

            return (ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_OBJECT_NAME);
        } catch (NotBoundException e) {
            throw new CustomException("An error occurred while creating the remote service: ", e);
        }
    }
}
