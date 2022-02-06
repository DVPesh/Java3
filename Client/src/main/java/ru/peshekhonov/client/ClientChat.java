package ru.peshekhonov.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.peshekhonov.client.controllers.AuthController;
import ru.peshekhonov.client.controllers.ClientController;
import ru.peshekhonov.client.controllers.UsernameController;
import ru.peshekhonov.client.model.Network;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientChat extends Application {

    public static ClientChat INSTANCE;

    private Stage primaryStage;
    private Stage authStage;
    private Stage usernameStage;
    private FXMLLoader chatWindowLoader;
    private FXMLLoader authLoader;
    private FXMLLoader usernameLoader;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void init() throws Exception {
        INSTANCE = this;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;

        initViews();

        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Network.getInstance().close();
            }
        });

        getAuthController().initializeMessageHandler();

        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Network network = Network.getInstance();
                    Label label = getChatController().label;
                    if (network.isConnected()) {
                        label.setText("установлено");
                    } else {
                        label.setText("не установлено");
                        getChatController().setUsername("");
                        getChatController().clearUserList();
                    }
                    if (network.isConnected()
                            && network.isReadMessageListenerPresent(getChatController().getReadMessageListener())) {
                        getChatController().setSendButtonText("Отправить");
                    } else {
                        getChatController().setSendButtonText("Войти");
                    }
                });
            }
        }, 100, 1000);

        getChatStage().show();
        getUsernameStage().show();
        getUsernameStage().hide();
        getAuthStage().show();
    }

    private void initViews() throws IOException {
        initChatWindow();
        initAuthDialog();
        initUsernameDialog();
    }

    private void initChatWindow() throws IOException {
        chatWindowLoader = new FXMLLoader();
        chatWindowLoader.setLocation(ClientChat.class.getResource("chat-template.fxml"));
        Parent root = chatWindowLoader.load();
        this.primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Онлайн чат");
        getChatController().setUsername("");
    }

    private void initAuthDialog() throws IOException {
        authLoader = new FXMLLoader();
        authLoader.setLocation(ClientChat.class.getResource("authDialog.fxml"));
        Parent authDialogPanel = authLoader.load();
        authStage = new Stage();
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setScene(new Scene(authDialogPanel));
        authStage.setTitle("Авторизация");
        authStage.setResizable(false);
    }

    private void initUsernameDialog() throws IOException {
        usernameLoader = new FXMLLoader();
        usernameLoader.setLocation(ClientChat.class.getResource("usernameDialog.fxml"));
        Parent usernameDialogPanel = usernameLoader.load();
        usernameStage = new Stage();
        usernameStage.initOwner(primaryStage);
        usernameStage.initModality(Modality.WINDOW_MODAL);
        usernameStage.setScene(new Scene(usernameDialogPanel));
        usernameStage.setTitle("Изменение имени пользователя");
        usernameStage.setResizable(false);
    }

    public void switchToMainChatWindowAfterAuthOk(String username) {
        getChatController().setUsername(username);
        getChatController().initializeMessageHandler();
        getAuthController().close();
        getAuthStage().close();
    }

    public void switchToMainChatWindowAfterChangeUsernameOk(String username) {
        getChatController().setUsername(username);
        getUsernameController().close();
    }

    public Stage getChatStage() {
        return this.primaryStage;
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public Stage getUsernameStage() {
        return usernameStage;
    }

    public AuthController getAuthController() {
        return authLoader.getController();
    }

    public ClientController getChatController() {
        return chatWindowLoader.getController();
    }

    public UsernameController getUsernameController() {
        return usernameLoader.getController();
    }

}
