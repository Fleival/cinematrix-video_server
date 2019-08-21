package com.denspark.gui;

import com.denspark.gui.console.ConsoleApplication;
import com.denspark.gui.console.ConsoleView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {
    private boolean pauseBeforeExit = true;
    private Stage stage;
    final ConsoleView console = new ConsoleView();

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        scene = new Scene(loadFXML("primary"));
        final URL styleSheetUrl = getStyleSheetUrl();
        if (styleSheetUrl != null) {
            scene.getStylesheets().add(styleSheetUrl.toString());
        }
        AnchorPane anchorPane = (AnchorPane)scene.lookup("#mainPane");
        anchorPane.getChildren().add(console);
        console.setPrefWidth(anchorPane.getPrefWidth());
        console.setLayoutX(0);
        console.setLayoutY(250);
        stage.setScene(scene);
        stage.show();

        System.setOut(console.getOut());
        System.setIn(console.getIn());
        System.setErr(console.getOut());
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    protected URL getStyleSheetUrl() {
        final String styleSheetName = "style.css";
        URL url = getClass().getResource(styleSheetName);
        if (url != null) {
            return url;
        }
        url = App.class.getResource(styleSheetName);
        if (url != null) {
            return url;
        }
        return null;
    }

}