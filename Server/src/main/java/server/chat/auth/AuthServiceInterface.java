package server.chat.auth;

import java.sql.SQLException;

public interface AuthServiceInterface {

    String ERROR_MESSAGE = "Database access error occurs";

    void start() throws SQLException;

    void stop();

    String getUsernameByLoginAndPassword(String login, String password) throws SQLException;

    void changeUsername(String login, String username) throws SQLException;

    boolean doesUsernameExist(String username) throws SQLException;

}
