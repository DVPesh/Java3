package server.chat;

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

        new Thread(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                System.err.println("Failed to process message from client");
                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
                }
            }
        }).start();

    }

    private void authenticate() throws IOException {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                        System.out.println("Closing the connection by timeout");
                    }
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
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
                AuthCommandData data = (AuthCommandData) command.getData();
                String login = data.getLogin();
                String password = data.getPassword();
                String userName = null;
                try {
                    userName = server.getAuthService().getUsernameByLoginAndPassword(login, password);
                } catch (SQLException e) {
                    System.err.println("Database access error occurs");
                    e.printStackTrace();
                    sendCommand(Command.errorCommand("Ошибка обращения к базе данных сервера"));
                    continue;
                }
                if (userName == null) {
                    sendCommand(Command.errorCommand("Некорректные логин и пароль"));
                } else if (server.isUsernameBusy(userName)) {
                    sendCommand(Command.errorCommand("Такой пользователь уже существует!"));
                } else {
                    timerTask.cancel();
                    timer.purge();
                    this.user = new User(login, password, userName);
                    sendCommand(Command.authOkCommand(userName));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
            System.err.println("Failed to read Command class");
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
                    return;
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String privateMessage = data.getMessage();
                    server.sendPrivateMessage(this, recipient, privateMessage);
                    break;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    processMessage(data.getMessage());
                    break;
                }
                case CHANGE_USERNAME: {
                    ChangeUsernameCommandData data = (ChangeUsernameCommandData) command.getData();
                    String login = data.getLogin();
                    String password = data.getPassword();
                    String username = data.getUsername();
                    if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                        try {
                            if (!server.getAuthService().doesUsernameExist(username)) {
                                server.getAuthService().changeUsername(login, username);
                            } else {
                                sendCommand(Command.errorCommand("Пользователь с таким именем уже существует"));
                                break;
                            }
                        } catch (SQLException e) {
                            System.err.println("Database access error occurs");
                            e.printStackTrace();
                            sendCommand(Command.errorCommand("Ошибка обращения к базе данных сервера"));
                            break;
                        }
                        this.user = new User(login, password, username);
                        server.changeUsername(this);
                    } else {
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
