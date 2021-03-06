package PlaylistExporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlaylistExporter extends Application {

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("disarmed.css").toExternalForm());
        stage = primaryStage;
        stage.setTitle("PlaylistExporter 2.0 by Rudi Novik");
        stage.setScene(scene);
        stage.show();
    }
}
