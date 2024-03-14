package qupath.ext.template;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qupath.ext.template.ui.InterfaceController;
import qupath.fx.dialogs.Dialogs;
import qupath.fx.prefs.controlsfx.PropertyItemBuilder;
import qupath.lib.common.Version;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.extensions.GitHubProject;
import qupath.lib.gui.extensions.QuPathExtension;
import qupath.lib.gui.prefs.PathPrefs;

import java.io.IOException;


/**
 * This is a demo to provide a template for creating a new QuPath extension.
 * <p>
 * It doesn't do much - it just shows how to add a menu item and a preference.
 * See the code and comments below for more info.
 * <p>
 * <b>Important!</b> For your extension to work in QuPath, you need to make sure the name &amp; package
 * of this class is consistent with the file
 * <pre>
 *     /resources/META-INF/services/qupath.lib.gui.extensions.QuPathExtension
 * </pre>
 */
public class DemoExtension implements QuPathExtension, GitHubProject {
	
	private static final Logger logger = LoggerFactory.getLogger(DemoExtension.class);

	/**
	 * Display name for your extension
	 * TODO: define this
	 */
	private static final String EXTENSION_NAME = "My Java extension";

	/**
	 * Short description, used under 'Extensions > Installed extensions'
	 * TODO: define this
	 */
	private static final String EXTENSION_DESCRIPTION = "This is just a demo to show how extensions work";

	/**
	 * QuPath version that the extension is designed to work with.
	 * This allows QuPath to inform the user if it seems to be incompatible.
	 * TODO: define this
	 */
	private static final Version EXTENSION_QUPATH_VERSION = Version.parse("v0.5.0");

	/**
	 * GitHub repo that your extension can be found at.
	 * This makes it easier for users to find updates to your extension.
	 * If you don't want to support this feature, you can remove
	 * references to GitHubRepo and GitHubProject from your extension.
	 * TODO: define this
	 */
	private static final GitHubRepo EXTENSION_REPOSITORY = GitHubRepo.create(
			EXTENSION_NAME, "myGitHubUserName", "myGitHubRepo");

	/**
	 * Flag whether the extension is already installed (might not be needed... but we'll do it anyway)
	 */
	private boolean isInstalled = false;

	/**
	 * A 'persistent preference' - showing how to create a property that is stored whenever QuPath is closed.
	 * This preference will be managed in the main QuPath GUI preferences window.
	 */
	private static BooleanProperty enableExtensionProperty = PathPrefs.createPersistentPreference(
			"enableExtension", true);


	/**
	 * Another 'persistent preference'.
	 * This one will be managed using a GUI element created by the extension.
	 * We use {@link Property<Integer>} rather than {@link IntegerProperty}
	 * because of the type of GUI element we use to manage it.
	 */
	private static Property<Integer> numThreadsProperty = PathPrefs.createPersistentPreference(
			"demo.num.threads", 1).asObject();

	/**
	 * An example of how to expose persistent preferences to other classes in your extension.
	 * @return The persistent preference, so that it can be read or set somewhere else.
	 */
	public static Property<Integer> numThreadsProperty() {
		return numThreadsProperty;
	}

	/**
	 * Create a stage for the extension to display
	 */
	private Stage stage;

	@Override
	public void installExtension(QuPathGUI qupath) {
		if (isInstalled) {
			logger.debug("{} is already installed", getName());
			return;
		}
		isInstalled = true;
		addPreference(qupath);
		addPreferenceToPane(qupath);
		addMenuItem(qupath);
	}

	/**
	 * Demo showing how to add a persistent preference to the QuPath preferences pane.
	 * The preference will be in a section of the preference pane based on the
	 * category you set. The description is used as a tooltip.
	 * @param qupath The currently running QuPathGUI instance.
	 */
	private void addPreferenceToPane(QuPathGUI qupath) {
		var propertyItem = new PropertyItemBuilder<>(enableExtensionProperty, Boolean.class)
				.name("Enable extension")
				.category("Demo extension")
				.description("Enable the demo extension")
				.build();
		qupath.getPreferencePane()
				.getPropertySheet()
				.getItems()
				.add(propertyItem);
	}

	/**
	 * Demo showing how to add a persistent preference.
	 * This will be loaded whenever QuPath launches, with the value retained unless
	 * the preferences are reset.
	 * However, users will not be able to edit it unless you create a GUI
	 * element that corresponds with it
	 * @param qupath The currently running QuPathGUI instance.
	 */
	private void addPreference(QuPathGUI qupath) {
		qupath.getPreferencePane().addPropertyPreference(
				enableExtensionProperty,
				Boolean.class,
				"Enable my extension",
				EXTENSION_NAME,
				"Enable my extension");
	}

	/**
	 * Demo showing how a new command can be added to a QuPath menu.
	 * @param qupath
	 */
	private void addMenuItem(QuPathGUI qupath) {
		var menu = qupath.getMenu("Extensions>" + EXTENSION_NAME, true);
		MenuItem menuItem = new MenuItem("My menu item");
		menuItem.setOnAction(e -> createStage());
		menuItem.disableProperty().bind(enableExtensionProperty.not());
		menu.getItems().add(menuItem);
	}

	/**
	 * Demo showing how to create a new stage with a JavaFX FXML interface.
	 */
	private void createStage() {
		if (stage == null) {
			try {
				stage = new Stage();
				Scene scene = new Scene(InterfaceController.createInstance());
				stage.setScene(scene);
			} catch (IOException e) {
				Dialogs.showErrorMessage("Extension Error", "GUI loading failed");
				logger.error("Unable to load extension interface FXML", e);
			}
		}
		stage.show();
	}

	@Override
	public String getName() {
		return EXTENSION_NAME;
	}

	@Override
	public String getDescription() {
		return EXTENSION_DESCRIPTION;
	}
	
	@Override
	public Version getQuPathVersion() {
		return EXTENSION_QUPATH_VERSION;
	}

	@Override
	public GitHubRepo getRepository() {
		return EXTENSION_REPOSITORY;
	}
}
