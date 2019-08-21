package com.denspark.gui;


import com.denspark.CinematrixVideoApplication;
import javafx.fxml.FXML;

import java.io.IOException;

public class PrimaryController {
    CinematrixVideoApplication server = new CinematrixVideoApplication();

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    public void startServer() {
        String[] args = {"server", "config.yml"};
        try {
            server.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void stopServer() {
        try {
            server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
