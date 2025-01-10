package qupath.ext.template.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import qupath.ext.template.DemoExtension;
import qupath.fx.dialogs.Dialogs;

import java.io.IOException;

/**
 * Controller for UI pane contained in interface.fxml
 */

public class InterfaceController extends VBox {


    @FXML
    private Spinner<Integer> integerOptionSpinner;

    /**
     * Create a new instance of the interface controller.
     * @return a new instance of the interface controller
     * @throws IOException If reading the extension FXML files fails.
     */
    public static InterfaceController createInstance() throws IOException {
        return new InterfaceController();
    }

    private InterfaceController() throws IOException {
        var url = InterfaceController.class.getResource("interface.fxml");
        FXMLLoader loader = new FXMLLoader(url, DemoExtension.getResources());
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        // For extensions with a small number of options,
        // or with options that are very important for how the extension works,
        // it may be better to present them all to the user in the main extension GUI,
        // binding them to GUI elements, so they are updated when the user interacts with
        // the GUI, and so that the GUI elements are updated if the preference changes
        integerOptionSpinner.getValueFactory().valueProperty().bindBidirectional(DemoExtension.numericOptionProperty());
        integerOptionSpinner.getValueFactory().valueProperty().addListener((observableValue, oldValue, newValue) -> {
            Dialogs.showInfoNotification(
                    DemoExtension.getResources().getString("title"),
                    String.format(DemoExtension.getResources().getString("option-set-to"), newValue));
        });
    }

    @FXML
    private void runDemoExtension() {
        Dialogs.showInfoNotification(
                DemoExtension.getResources().getString("run.title"),
                DemoExtension.getResources().getString("run.message")
        );
    }


}
