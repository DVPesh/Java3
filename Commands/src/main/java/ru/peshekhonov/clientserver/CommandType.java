package ru.peshekhonov.clientserver;

public enum CommandType {
    AUTH,
    AUTH_OK,
    ERROR,
    PUBLIC_MESSAGE,
    PRIVATE_MESSAGE,
    CLIENT_MESSAGE,
    END,
    UPDATE_USER_LIST,
    CHANGE_USERNAME,
    CHANGE_USERNAME_OK
}
