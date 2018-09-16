package ui.menu;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.properties.PropertiesManager;

/**
 * The controller, which handles the top menu actions
 */
public class MenuController {

    /**
     * The {@link MenuController} logger
     */
    private static Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

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
        this.langManager = new PropertiesManager("properties/lang");
    }

    public Parent getView(){
        return menuBar;
    }
}
