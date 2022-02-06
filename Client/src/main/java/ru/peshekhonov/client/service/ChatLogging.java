package ru.peshekhonov.client.service;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ChatLogging {

    public static ChatLogging instance;

    private PrintWriter printWriter;
    private final File chatLoggingFile;

    private final String login;

    public ChatLogging(String login) {
        this.login = login;
        String pathname = String.format("logging/history_%s.txt", login);
        chatLoggingFile = new File(pathname);
        try {
            if (!chatLoggingFile.exists()) {
                chatLoggingFile.getParentFile().mkdirs();
                if (chatLoggingFile.createNewFile()) {
                    System.out.printf("File %s was created%n", chatLoggingFile.getAbsolutePath());
                }
            }
            this.printWriter = new PrintWriter(new BufferedWriter(
                    new FileWriter(chatLoggingFile, StandardCharsets.UTF_8, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendMessage(String message) {
        if (printWriter != null) {
            printWriter.print(message);
            printWriter.flush();
        }
    }

    public void close() {
        if (printWriter != null) printWriter.close();
    }

    public String readLastChatLoggingRows(int rowsNumber) {
        List<String> lastChatLoggingRows = new LinkedList<>();

        try (ReversedLinesFileReader fileReader = new ReversedLinesFileReader(chatLoggingFile, StandardCharsets.UTF_8)) {
            for (int i = 0; i < rowsNumber; i++) {
                String row = fileReader.readLine();
                if (row == null) break;
                lastChatLoggingRows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        Collections.reverse(lastChatLoggingRows);

        return String.join(System.lineSeparator(), lastChatLoggingRows) + System.lineSeparator();
    }

    public String getLogin() {
        return this.login;
    }

}
