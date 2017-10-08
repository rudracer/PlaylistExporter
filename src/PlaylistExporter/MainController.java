package PlaylistExporter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * Created by Rudi on 08.10.2017.
 */
public class MainController {
    @FXML
    Button playlistBtn;

    @FXML
    Button targetBtn;

    @FXML
    Button goBtn;

    @FXML
    Label playlistLbl;

    @FXML
    Label targetLbl;

    @FXML
    TextArea statusTxt;

    //Attributes
    File playlist;
    File target;

    @FXML
    protected void playlistBtnClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Playlist File");
        playlist = chooser.showOpenDialog(playlistBtn.getScene().getWindow());
        System.out.println(playlist.getPath());
    }
}
