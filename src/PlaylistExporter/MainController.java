package PlaylistExporter;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudi on 08.10.2017.
 */
public class MainController {
    @FXML
    Button playlistBtn;

    @FXML
    Button targetBtn;

    @FXML
    Button helpBtn;

    @FXML
    ProgressBar goBar;

    @FXML
    Label playlistLbl;

    @FXML
    Label targetLbl;

    @FXML
    Label goLbl;

    //Attributes
    File playlist;
    File target;
    int armed = 0; //simulate button properties with progressBar;
    //TODO: make focus possible

    public void initialize() {
        //goBar.getStyleClass().clear();
//        goBar.getStyleClass().removeAll("armed", "highlighted");
//        goBar.getStyleClass().add("disarmed");
        //goBar.getStyleClass().add("button");
        //goBar.setStyle("-fx-background-color: #d0d0d0");
        playlistLbl.setStyle("-fx-background-color: #dddddd");
        targetLbl.setStyle("-fx-background-color: #dddddd");
    }

    @FXML
    protected void playlistBtnClick(ActionEvent event) {
        goBar.progressProperty().unbind();
        goLbl.textProperty().unbind();
        goBar.setProgress(0);
        goLbl.setText("GO!");
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Playlist File");
        chooser.getExtensionFilters().add(extensionFilter);
        File playlistTemp = chooser.showOpenDialog(playlistBtn.getScene().getWindow());
        if (playlistTemp != null) playlist = playlistTemp;
        playlistLbl.setText(playlist == null ? "" : playlist.getPath());
    }

    @FXML
    protected void targetBtnClick(ActionEvent event) {
        goBar.progressProperty().unbind();
        goLbl.textProperty().unbind();
        goBar.setProgress(0);
        goLbl.setText("GO!");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Target Directory");
        File targetTemp = chooser.showDialog(targetBtn.getScene().getWindow());
        if (targetTemp != null) target = targetTemp;
        targetLbl.setText(target == null ? "" : target.getPath());
    }


    @FXML
    protected void goBarClick() {
        if (armed < 2) return; //simulate button behaviour
        Task task = new Task<Void>() {
            public Void call() throws InterruptedException, ParserConfigurationException, IOException, SAXException {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;
                List<String> paths;

                db = dbf.newDocumentBuilder();
                Document doc = db.parse(playlist);
                NodeList nodeList = doc.getElementsByTagName("dict");
                paths = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    NodeList childList = node.getChildNodes();
                    for (int j = 0; j < childList.getLength(); j++) {
                        Node child = childList.item(j);

                        //at this level, nodeName can be "string", "key" or "integer"
                        if (child.getNodeName().equals("key")) {
                            //the Node with nodeName key has a child with the
                            //actual key as its nodeValue
                            Node keyNode = child.getFirstChild();
                            //System.out.println(keyNode.getNodeValue());
                            if (keyNode.getNodeValue().equals("Location")) {
                                //if value is "Location", this means that the actual location
                                //is in the node after this node's parent "key"-node
                                Node location = childList.item(j + 1).getFirstChild();

                                if (!paths.contains(location.getNodeValue())) {
                                    paths.add(location.getNodeValue());
                                }
                            }
                        }
                    }
                }

                if (paths != null) {
                    updateProgress(0, paths.size());
                    updateMessage("0.0%");
                    int i = 0;
                    for (String s : paths) {
                        //Windows
                        String sourceStr = s.replaceAll("file://localhost", "");
                        //Mac
                        sourceStr = sourceStr.replaceAll("file:/", "");
                        try {
                            sourceStr = URLDecoder.decode(sourceStr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Unsupported Encoding!");
                                alert.setContentText(
                                        "Check that your playlist file's encoding is " +
                                                "\"UTF-8\" and try again!");
                                alert.showAndWait();
                            });
                        }
                        File source = new File(sourceStr);
                        try {
                            i++;
                            updateProgress(i, paths.size());
                            updateMessage(""+i/(double)paths.size()*100+"%");
                            //Thread.sleep(500); //Test
                            FileUtils.copyFileToDirectory(source, target);
                        } catch (IOException e) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setContentText(
                                        "Ooops. Something went wrong. \n" +
                                                "Maybe a path is incorrect, or an external " +
                                                "drive is missing.\n" +
                                                "Check this and try again!\n" +
                                                "Hint: You could re-export the playlist " +
                                                "file from iTunes.");
                                alert.showAndWait();
                            });
                        }
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText(
                            "Ooops. Something went wrong. " +
                                    "Check your playlist file and try again!");
                    alert.showAndWait();
                }
                return null;
            }
        };
        goBar.progressProperty().bind(task.progressProperty());
        goLbl.textProperty().bind(task.messageProperty());
        task.setOnFailed(evt -> {
            Throwable e = task.getException();
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle(e.getClass().getSimpleName());
//            alert.setContentText(e.getLocalizedMessage());
//            alert.showAndWait();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Reminder");
            alert.setContentText("Please choose both playlist file and target directory.");
            alert.showAndWait();
            goBar.progressProperty().unbind();
            goLbl.textProperty().unbind();
            goBar.setProgress(0);
            goLbl.setText("GO!");
        });
        try {
            new Thread(task).start();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Reminder");
            alert.setContentText("Please choose both playlist file and target directory.");
            alert.showAndWait();
        }
    }

    public void goBarArm() {
        goBar.getScene().getStylesheets().removeAll(
                getClass().getResource("armed.css").toExternalForm(),
                getClass().getResource("disarmed.css").toExternalForm(),
                getClass().getResource("highlighted.css").toExternalForm()
        );
//        goBar.getStyleClass().removeAll("highlighted", "armed", "disarmed");
        armed++;
        if (armed == 1){
            goBar.getScene().getStylesheets().add(
                    getClass().getResource("highlighted.css").toExternalForm()
            );
        }
        if (armed == 2){
            goBar.getScene().getStylesheets().add(
                    getClass().getResource("armed.css").toExternalForm()
            );
        }
//        if (armed == 1) goBar.getStyleClass().add("highlighted");//goBar.setStyle("-fx-background-color: derive(#d0d0d0, 20%)");
//        if (armed == 2) goBar.getStyleClass().add("armed");//goBar.setStyle("-fx-background-color: derive(#d0d0d0, -20%)");
//        System.out.println("armed: " + armed);
    }

    public void goBarDisarm() {
        goBar.getScene().getStylesheets().removeAll(
                getClass().getResource("armed.css").toExternalForm(),
                getClass().getResource("disarmed.css").toExternalForm(),
                getClass().getResource("highlighted.css").toExternalForm()
        );
        //goBar.getStyleClass().removeAll("highlighted", "armed", "disarmed");
        armed = 0;
//        goBar.getStyleClass().add("disarmed");//goBar.setStyle("-fx-background-color: #d0d0d0");
        goBar.getScene().getStylesheets().add(
                getClass().getResource("disarmed.css").toExternalForm()
        );
//        System.out.println("fully disarmed: " + armed);
    }

    public void helpBtnClick(ActionEvent event) throws IOException {
        Parent root;
        root = FXMLLoader.load(getClass().getResource("Help.fxml"));
        Stage stage = new Stage();
        stage.setTitle("PlaylistExporter - Help");
        stage.setScene(new Scene(root, 580, 250));
        stage.show();
    }
}
