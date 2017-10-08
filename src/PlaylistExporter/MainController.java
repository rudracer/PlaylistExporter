package PlaylistExporter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
        target = chooser.showDialog(targetBtn.getScene().getWindow());
        targetLbl.setText(target.getPath());
    }


    @FXML
    protected void goBtnClick(ActionEvent event) {

        statusTxt.setText("");
        statusTxt.setStyle("-fx-text-fill: black;");


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
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
            statusTxt.setStyle("-fx-text-fill: red;");
            statusTxt.setText("Reading playlist failed!");
        }

        if (paths != null) {

            statusTxt.appendText("\nCopying files to " + target.getPath() + "...\n");

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
                statusTxt.appendText("\nCopying " + source.getPath() + "...\n");
                try {
                    FileUtils.copyFileToDirectory(source, target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            statusTxt.appendText("\nFinished copying!");

        } else {
            statusTxt.setStyle("-fx-text-fill: red;");
            statusTxt.setText("Reading playlist failed!");
        }





    }
}
