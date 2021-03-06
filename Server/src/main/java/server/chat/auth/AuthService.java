package server.chat.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class AuthService implements AuthServiceInterface {

    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final static String DB_URL = "jdbc:sqlite:users.db";
    private final static String SQL_GET_USERNAME = "SELECT username FROM users WHERE login=? AND password=?";
    private final static String SQL_SET_USERNAME = "UPDATE users SET username = ? WHERE login = ?";
    private final static String SQL_CHECK_USERNAME = "SELECT username FROM users WHERE username = ?";

    private PreparedStatement getUsernamePrepared;
    private PreparedStatement setUsernamePrepared;
    private PreparedStatement checkUsernamePrepared;
    private Connection connection;

    @Override
    public void start() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        getUsernamePrepared = connection.prepareStatement(SQL_GET_USERNAME);
        setUsernamePrepared = connection.prepareStatement(SQL_SET_USERNAME);
        checkUsernamePrepared = connection.prepareStatement(SQL_CHECK_USERNAME);
    }

    @Override
    public void stop() {
        try {
            if (getUsernamePrepared != null) {
                getUsernamePrepared.close();
            }
        } catch (SQLException e) {
            LOGGER.error(ERROR_MESSAGE, e);
//            e.printStackTrace();
        }
        try {
            if (setUsernamePrepared != null) {
                setUsernamePrepared.close();
            }
        } catch (SQLException e) {
            LOGGER.error(ERROR_MESSAGE, e);
//            e.printStackTrace();
        }
        try {
            if (checkUsernamePrepared != null) {
                checkUsernamePrepared.close();
            }
        } catch (SQLException e) {
            LOGGER.error(ERROR_MESSAGE, e);
//            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error(ERROR_MESSAGE, e);
//            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException {
        getUsernamePrepared.setString(1, login);
        getUsernamePrepared.setString(2, password);
        ResultSet resultSet = getUsernamePrepared.executeQuery();
        if (resultSet.next()) return resultSet.getString("username");
        return null;
    }

    @Override
    public synchronized void changeUsername(String login, String username) throws SQLException {
        setUsernamePrepared.setString(1, username);
        setUsernamePrepared.setString(2, login);
        setUsernamePrepared.executeUpdate();
    }

    @Override
    public boolean doesUsernameExist(String username) throws SQLException {
        checkUsernamePrepared.setString(1, username);
        ResultSet resultSet = checkUsernamePrepared.executeQuery();
        return resultSet.next();
    }
}
