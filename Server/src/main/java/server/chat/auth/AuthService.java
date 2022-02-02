package server.chat.auth;

import java.sql.*;

public class AuthService implements AuthServiceInterface {

    private final static String DB_URL = "jdbc:sqlite:users.db";
    private final static String SQL_GET_USERNAME = "SELECT username FROM users WHERE login=? AND password=?";
    private final static String SQL_SET_USERNAME = "UPDATE users SET username = ? WHERE login = ?";

    private PreparedStatement getUsernamePrepared;
    private PreparedStatement setUsernamePrepared;
    private Connection connection;

    @Override
    public void start() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        getUsernamePrepared = connection.prepareStatement(SQL_GET_USERNAME);
        setUsernamePrepared = connection.prepareStatement(SQL_SET_USERNAME);
    }

    @Override
    public void stop() {
        try {
            if (getUsernamePrepared != null) {
                getUsernamePrepared.close();
            }
        } catch (SQLException e) {
            System.err.println(ERROR_MESSAGE);
            e.printStackTrace();
        }
        try {
            if (setUsernamePrepared != null) {
                setUsernamePrepared.close();
            }
        } catch (SQLException e) {
            System.err.println(ERROR_MESSAGE);
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println(ERROR_MESSAGE);
            e.printStackTrace();
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
}
