package ru.peshekhonov.clientserver.commands;

import java.io.Serializable;

public class ChangeUsernameOkCommandData implements Serializable {

    private final String username;

    public ChangeUsernameOkCommandData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
