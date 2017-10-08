package PlaylistExporter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
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
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Playlist File");
        chooser.getExtensionFilters().add(extensionFilter);
        playlist = chooser.showOpenDialog(playlistBtn.getScene().getWindow());
        playlistLbl.setText(playlist.getPath());
    }

    @FXML
    protected void targetBtnClick(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Target Directory");
        File target = chooser.showDialog(targetBtn.getScene().getWindow());
        targetLbl.setText(target.getPath());
    }
}
