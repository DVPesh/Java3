package ru.peshekhonov.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ru.peshekhonov.client.ClientChat;
import ru.peshekhonov.client.dialogs.Dialogs;
import ru.peshekhonov.client.model.Network;
import ru.peshekhonov.client.model.ReadCommandListener;
import ru.peshekhonov.client.service.ChatLogging;
import ru.peshekhonov.clientserver.Command;
import ru.peshekhonov.clientserver.CommandType;
import ru.peshekhonov.clientserver.commands.ClientMessageCommandData;
import ru.peshekhonov.clientserver.commands.UpdateUserListCommandData;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ClientController {

    private final static String TO_ALL_USERS_ITEM = "всем";
    private final static int LAST_CHAT_LOGGING_ROWS_LOADED_QUANTITY = 100;

    @FXML
    public Label label;
    @FXML
    public Label usernameLabel;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<String> userList;

    private ReadCommandListener readMessageListener;
    public String login;

    public void sendMessage() {
        Network network = Network.getInstance();
        ClientChat clientChat = ClientChat.INSTANCE;
        Stage authStage = clientChat.getAuthStage();
        Stage chatStage = clientChat.getChatStage();
        AuthController authController = clientChat.getAuthController();
        UsernameController usernameController = clientChat.getUsernameController();

        if (!network.isConnected() || network.isReadMessageListenerPresent(authController.getReadMessageListener())) {

            if (!network.isReadMessageListenerPresent(authController.getReadMessageListener())) {
                authController.initializeMessageHandler();
            }

            if (network.isReadMessageListenerPresent(usernameController.getReadMessageListener())) {
                network.removeReadMessageListener(usernameController.getReadMessageListener());
            }

            if (readMessageListener != null) network.removeReadMessageListener(readMessageListener);

            if (!authStage.isShowing()) {
                authStage.setX(chatStage.getX() + (chatStage.getWidth() - authStage.getWidth()) / 2);
                authStage.setY(chatStage.getY() + (chatStage.getHeight() - authStage.getHeight()) / 2);
                authStage.show();
            }

            textField.clear();
            return;
        }

        String message = textField.getText().trim();

        if (message.isEmpty()) {
            textField.clear();
            return;
        }

        String recipient = null;
        if (!userList.getSelectionModel().isEmpty()) {
            recipient = userList.getSelectionModel().getSelectedItem();
        }

        try {
            if (message.equals("/END")) {
                System.out.println("The client broke the connection");
                network.sendEndCommand();
                setUsername("");
            } else if (recipient == null || recipient.equals(TO_ALL_USERS_ITEM)) {
                System.out.println("The client has sent a broadcast message");
                network.sendMessage(message);
            } else {
                network.sendPrivateMessage(recipient, message);
            }

        } catch (IOException e) {
            Dialogs.NetworkError.SEND_MESSAGE.show();
            e.printStackTrace();
        }

        appendMessageToChat("Я", message);
    }

    private void appendMessageToChat(String sender, String message) {
        String oldChatWindowContent = textArea.getText();

        textArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
        textArea.appendText(System.lineSeparator());

        if (sender != null) {
            textArea.appendText(sender + ":");
            textArea.appendText(System.lineSeparator());
        }

        textArea.appendText(message);
        textArea.appendText(System.lineSeparator());
        textArea.appendText(System.lineSeparator());
        textField.setFocusTraversable(true);
        textField.clear();

        ChatLogging.instance.appendMessage(textArea.getText(oldChatWindowContent.length(), textArea.getLength()));
    }

    public void initializeMessageHandler() {
        readMessageListener = Network.getInstance().addReadMessageListener(new ReadCommandListener() {
            @Override
            public void processReceivedCommand(Command command) {
                if (ChatLogging.instance == null) {
                    ChatLogging.instance = new ChatLogging(login);
                    loadLastChatLoggingRows();
                } else if (!login.equals(ChatLogging.instance.getLogin())) {
                    ChatLogging.instance.close();
                    ChatLogging.instance = new ChatLogging(login);
                    loadLastChatLoggingRows();
                }

                if (command.getType() == CommandType.CLIENT_MESSAGE) {
                    ClientMessageCommandData data = (ClientMessageCommandData) command.getData();
                    appendMessageToChat(data.getSender(), data.getMessage());
                } else if (command.getType() == CommandType.UPDATE_USER_LIST) {
                    UpdateUserListCommandData data = (UpdateUserListCommandData) command.getData();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (data.getUsers().size() > 1) data.getUsers().add(TO_ALL_USERS_ITEM);
                            userList.setItems(FXCollections.observableList(data.getUsers()));
                            userList.getSelectionModel().select(TO_ALL_USERS_ITEM);
                        }
                    });
                }
            }
        });
    }

    public ReadCommandListener getReadMessageListener() {
        return readMessageListener;
    }

    public void nickOnMousePressed(MouseEvent mouseEvent) {
        showUsernameChangeForm();
    }

    public void usernameOnMousePressed(MouseEvent mouseEvent) {
        showUsernameChangeForm();
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public String getUsername() {
        return usernameLabel.getText();
    }

    public void setSendButtonText(String message) {
        sendButton.setText(message);
    }

    public void clearUserList() {
        if (!userList.getItems().isEmpty()) userList.getItems().clear();
    }

    private void showUsernameChangeForm() {
        Network network = Network.getInstance();
        ClientChat clientChat = ClientChat.INSTANCE;
        Stage usernameStage = clientChat.getUsernameStage();
        Stage chatStage = clientChat.getChatStage();
        UsernameController usernameController = clientChat.getUsernameController();

        if (network.isConnected() && network.isReadMessageListenerPresent(getReadMessageListener())) {

            if (!network.isReadMessageListenerPresent(usernameController.getReadMessageListener())) {
                usernameController.initializeMessageHandler();
            }

            if (!usernameStage.isShowing()) {
                usernameStage.setX(chatStage.getX() + (chatStage.getWidth() - usernameStage.getWidth()) / 2);
                usernameStage.setY(chatStage.getY() + (chatStage.getHeight() - usernameStage.getHeight()) / 2);
                usernameStage.show();
            }
        }
    }

    public void loadLastChatLoggingRows() {
        textArea.clear();
        textArea.setText(ChatLogging.instance.readLastChatLoggingRows(LAST_CHAT_LOGGING_ROWS_LOADED_QUANTITY));
    }
}
