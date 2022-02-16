package server.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.peshekhonov.clientserver.Command;
import ru.peshekhonov.clientserver.CommandType;
import ru.peshekhonov.clientserver.commands.AuthCommandData;
import ru.peshekhonov.clientserver.commands.ChangeUsernameCommandData;
import ru.peshekhonov.clientserver.commands.PrivateMessageCommandData;
import ru.peshekhonov.clientserver.commands.PublicMessageCommandData;
import server.chat.auth.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {

    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private final MyServer server;
    private final Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private User user;
    private final static Timer timer = new Timer(true);

    public ClientHandler(MyServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        server.getExecutorService().execute(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                LOGGER.error("Failed to process message from client", e);
//                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    LOGGER.error("Failed to close connection", e);
//                    e.printStackTrace();
                }
            }
        });

    }

    private void authenticate() throws IOException {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                        LOGGER.warn("Closing the connection by timeout");
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to close connection", e);
//                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask, 120000);

        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            if (command.getType() == CommandType.AUTH) {
                LOGGER.info("AUTH");
                AuthCommandData data = (AuthCommandData) command.getData();
                String login = data.getLogin();
                String password = data.getPassword();
                String userName = null;
                try {
                    userName = server.getAuthService().getUsernameByLoginAndPassword(login, password);
                } catch (SQLException e) {
                    LOGGER.error("Database access error occurs", e);
//                    e.printStackTrace();
                    LOGGER.info("errorCommand");
                    sendCommand(Command.errorCommand("Ошибка обращения к базе данных сервера"));
                    continue;
                }
                if (userName == null) {
                    LOGGER.info("errorCommand");
                    sendCommand(Command.errorCommand("Некорректные логин и пароль"));
                } else if (server.isUsernameBusy(userName)) {
                    LOGGER.info("errorCommand");
                    sendCommand(Command.errorCommand("Такой пользователь уже существует!"));
                } else {
                    timerTask.cancel();
                    timer.purge();
                    this.user = new User(login, password, userName);
                    LOGGER.info("authOkCommand");
                    sendCommand(Command.authOkCommand(userName));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOGGER.error("Delay error", e);
                    }
                    server.subscribe(this);
                    return;
                }
            }
        }
    }

    private Command readCommand() throws IOException {
        Command command = null;
        try {
            command = (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            LOGGER.error("Failed to read Command class", e);
//            e.printStackTrace();
        }
        return command;
    }

    private void closeConnection() throws IOException {
        if (!clientSocket.isClosed()) {
            server.unsubscribe(this);
            clientSocket.close();
        }
    }

    private void readMessages() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            switch (command.getType()) {
                case END:
                    LOGGER.info("End");
                    return;
                case PRIVATE_MESSAGE: {
                    LOGGER.info("Private message");
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String privateMessage = data.getMessage();
                    server.sendPrivateMessage(this, recipient, privateMessage);
                    break;
                }
                case PUBLIC_MESSAGE: {
                    LOGGER.info("Public message");
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    processMessage(data.getMessage());
                    break;
                }
                case CHANGE_USERNAME: {
                    LOGGER.info("Change username");
                    ChangeUsernameCommandData data = (ChangeUsernameCommandData) command.getData();
                    String login = data.getLogin();
                    String password = data.getPassword();
                    String username = data.getUsername();
                    if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                        try {
                            if (!server.getAuthService().doesUsernameExist(username)) {
                                server.getAuthService().changeUsername(login, username);
                            } else {
                                LOGGER.info("errorCommand");
                                sendCommand(Command.errorCommand("Пользователь с таким именем уже существует"));
                                break;
                            }
                        } catch (SQLException e) {
                            LOGGER.error("Database access error occurs", e);
//                            e.printStackTrace();
                            LOGGER.info("errorCommand");
                            sendCommand(Command.errorCommand("Ошибка обращения к базе данных сервера"));
                            break;
                        }
                        this.user = new User(login, password, username);
                        server.changeUsername(this);
                    } else {
                        LOGGER.info("errorCommand");
                        sendCommand(Command.errorCommand("Некорректные логин и пароль"));
                    }
                }
            }
        }
    }

    private void processMessage(String message) throws IOException {
        this.server.broadcastMessage(message, this);
    }

    public void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public String getUserName() {
        return user.getUserName();
    }
}
