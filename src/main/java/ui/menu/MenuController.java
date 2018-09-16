package ui.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.properties.PropertiesManager;

import java.util.Optional;

/**
 * The controller, which handles the top menu actions
 */
public class MenuController {

    /**
     * The {@link MenuController} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    /**
     * The pane with the top menu
     */
    @FXML
    public MenuBar menuBar;

    /**
     * The settings menu
     */
    @FXML
    public Menu menuSettings;

    /**
     * The language manager for setting the language in the program
     * TODO: Move the language change to another file: This should not happen here!
     */
    private PropertiesManager langManager;

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize(){
        LOGGER.info("Initialize");

        // TODO: Remove the next line!
        this.langManager = new PropertiesManager("properties/lang");


    }

    /**
     * Opens a dialog for choosing a language, which will be used throughout the application
     * @return language, if language was selected (clicked on ok); otherwise empty (clicked on cancel)
     */
    public Optional<String> showLanguageDialog(){
        Optional<String> op = Optional.empty();

        // TODO: Implement me!
        //try {
            // Load the fxml file
            FXMLLoader loader = new FXMLLoader();

        //}

        return op;
    }

    public Parent getView(){
        return menuBar;
    }
}
