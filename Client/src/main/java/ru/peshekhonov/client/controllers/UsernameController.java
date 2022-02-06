package ru.peshekhonov.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.peshekhonov.client.ClientChat;
import ru.peshekhonov.client.dialogs.Dialogs;
import ru.peshekhonov.client.model.Network;
import ru.peshekhonov.client.model.ReadCommandListener;
import ru.peshekhonov.clientserver.Command;
import ru.peshekhonov.clientserver.CommandType;
import ru.peshekhonov.clientserver.commands.ChangeUsernameOkCommandData;
import ru.peshekhonov.clientserver.commands.ErrorCommandData;

import java.io.IOException;

public class UsernameController {

    @FXML
    public TextField usernameField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button changeUsernameButton;

    private ReadCommandListener readMessageListener;

    public void executeUsernameChange(ActionEvent actionEvent) {
        Network network = getNetwork();
        ClientChat clientChat = ClientChat.INSTANCE;
        Stage usernameStage = clientChat.getUsernameStage();
        ClientController clientController = clientChat.getChatController();

        if (network.isConnected() && network.isReadMessageListenerPresent(clientController.getReadMessageListener())
                && clientController.getUsername().length() > 0) {

            String username = usernameField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();

            if (username == null || username.isBlank() || login == null || login.isBlank()
                    || password == null || password.isBlank()) {
                Dialogs.UsernameChangeError.EMPTY_CREDENTIALS.show();
                return;
            }
            if (username.equals(clientController.getUsername())) {
                usernameField.clear();
                loginField.clear();
                passwordField.clear();
                usernameStage.close();
                return;
            }

            try {
                Network.getInstance().sendUsernameChangeMessage(login, password, username);
                usernameField.clear();
                loginField.clear();
                passwordField.clear();
            } catch (IOException e) {
                Dialogs.NetworkError.SEND_MESSAGE.show();
                e.printStackTrace();
            }
        }
        usernameStage.close();
    }

    public void initializeMessageHandler() {
        readMessageListener = getNetwork().addReadMessageListener(new ReadCommandListener() {
            @Override
            public void processReceivedCommand(Command command) {
                if (command.getType() == CommandType.CHANGE_USERNAME_OK) {
                    ChangeUsernameOkCommandData data = (ChangeUsernameOkCommandData) command.getData();
                    String username = data.getUsername();
                    Platform.runLater(() -> ClientChat.INSTANCE.switchToMainChatWindowAfterChangeUsernameOk(username));
                } else if (command.getType() == CommandType.ERROR) {
                    ErrorCommandData data = (ErrorCommandData) command.getData();
                    Platform.runLater(() -> {
                        Dialogs.UsernameChangeError.INVALID_CREDENTIALS.show(data.getErrorMessage());
                    });
                }
            }
        });
    }

    public void close() {
        getNetwork().removeReadMessageListener(readMessageListener);
    }

    private Network getNetwork() {
        return Network.getInstance();
    }

    public ReadCommandListener getReadMessageListener() {
        return readMessageListener;
    }

}
