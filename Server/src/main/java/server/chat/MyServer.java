package server.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.peshekhonov.clientserver.Command;
import server.chat.auth.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    private final List<ClientHandler> clients = new ArrayList<>();
    private AuthService authService;
    private ExecutorService executorService;

    public AuthService getAuthService() {
        return authService;
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server has been started");
            authService = new AuthService();
            authService.start();
            executorService = Executors.newCachedThreadPool();

            while (true) {
                waitAndProcessClientConnection(serverSocket);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to bind port " + port, e);
//            e.printStackTrace();
        } catch (SQLException e) {
            LOGGER.error("Database access error occurs", e);
//            e.printStackTrace();
        } finally {
            if (authService != null) authService.stop();
            if (executorService != null) executorService.shutdown();
        }
    }

    private void waitAndProcessClientConnection(ServerSocket serverSocket) throws IOException {
        LOGGER.info("Waiting for new client connection");
        Socket clientSocket = serverSocket.accept();
        LOGGER.info("Client has been connected");
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender) {
                LOGGER.info("clientMessageCommand");
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), message));
            }
        }
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage) throws IOException {
        for (ClientHandler client : clients) {
            if (client != sender && client.getUserName().equals(recipient)) {
                LOGGER.info("clientMessageCommand");
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), privateMessage));
                break;
            }
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        notifyClientUserListUpdated();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        notifyClientUserListUpdated();
    }

    public synchronized void changeUsername(ClientHandler client) throws IOException {
        LOGGER.info("changeUsernameOkCommand");
        client.sendCommand(Command.changeUsernameOkCommand(client.getUserName()));
        notifyClientUserListUpdated();
    }

    private void notifyClientUserListUpdated() throws IOException {
        List<String> userListOnline = new ArrayList<>();

        for (ClientHandler client : clients) {
            userListOnline.add(client.getUserName());
        }

        for (ClientHandler client : clients) {
            LOGGER.info("updateUserListCommand");
            client.sendCommand(Command.updateUserListCommand(userListOnline));
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
