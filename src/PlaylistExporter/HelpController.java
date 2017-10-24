package PlaylistExporter;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class HelpController {
    @FXML
    TextArea helpText;

    public void initialize() {
        //String os = System.getProperty("os.name");
        helpText.setWrapText(true);
        StringBuilder sb = new StringBuilder("Export your desired playlist to a .xml-File by ");
        sb.append("performing the following steps: \n")
                .append("Click on the playlist that you want to export.\n")
                .append("If the menu bar is not visible, hit your keyboard's \"Alt\"-key.\n")
                .append("On the menu bar, click \"File\", ")
                .append("then \"Library\", ")
                .append("then \"Export Playlist...\".\n")
                .append("Type a file name and choose the file type \"XML (.xml)\".\n\n")
                .append("In PlaylistExporter, click on \"Choose Playlist...\" and choose your ")
                .append("previously exported XML file.\n")
                .append("Click on \"Choose target directory...\" and choose the location ")
                .append("you want to copy your playlist's files to.\n")
                .append("Click \"GO!\" when you're ready!");

            helpText.setText(sb.toString());
    }
}
