package qupath.ext.template;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller for UI pane contained in interface.fxml
 */

public class InterfaceController extends VBox {
    private static final ResourceBundle resources = ResourceBundle.getBundle("qupath.ext.instanseg.ui.strings");

    @FXML
    private TextField tfModelDirectory;

    public static InterfaceController createInstance() throws IOException {
        return new InterfaceController();
    }

    private InterfaceController() throws IOException {
        var url = InterfaceController.class.getResource("interface.fxml");
        FXMLLoader loader = new FXMLLoader(url, resources);
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML
    private void runDemoExtension() {
//        ExtensionCommand.runDemoExtension(tfModelDirectory.getText());
    }
}
