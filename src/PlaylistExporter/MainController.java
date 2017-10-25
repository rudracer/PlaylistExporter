package PlaylistExporter;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    protected void goBarClick() throws InterruptedException {
        Task task = new Task<Void>() {
            public Void call() throws InterruptedException {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;
                List<String> paths = null;
                try {
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
                } catch (SAXException | IOException | ParserConfigurationException e) {
                    e.printStackTrace();
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
                            e.printStackTrace();
                        }
                        File source = new File(sourceStr);
                        try {
                            i++;
                            updateProgress(i, paths.size());
                            updateMessage(""+i/(double)paths.size()*100+"%");
                            Thread.sleep(500); //Test
                            FileUtils.copyFileToDirectory(source, target);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //show message box with error
                }
                return null;
            }
        };
        goBar.progressProperty().bind(task.progressProperty());
        goLbl.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }

    public void goBarPressed() {

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
