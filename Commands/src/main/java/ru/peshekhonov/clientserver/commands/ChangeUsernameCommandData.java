package ru.peshekhonov.clientserver.commands;

import java.io.Serializable;

public class ChangeUsernameCommandData implements Serializable {
    private final String login;
    private final String password;
    private final String username;

    public ChangeUsernameCommandData(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

}
