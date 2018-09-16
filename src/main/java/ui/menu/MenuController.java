package ui.menu;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.error.DialogHelper;
import util.properties.PropertiesManager;

import java.util.List;

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
     * The language choose prompt
     */
    @FXML
    public MenuItem menuItemLanguage;

    /**
     * The language manager
     */
    private PropertiesManager langManager;

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize(){
        LOGGER.info("Initialize");

        langManager = new PropertiesManager("properties/lang");

        // Set the click listener for the 'Choose language' menu
        menuItemLanguage.setOnAction(event -> showLanguageDialog());
    }

    /**
     * Opens a dialog for choosing a language, which will be used throughout the application
     */
    private void showLanguageDialog(){

        LOGGER.info("Show language dialog");

        String languageText = "Language";

        List<String> languages = langManager.getAllStrings(languageText);

        // TODO: Check how you can notify the main application, that the language has changed
        DialogHelper.createChoiceDialog(languageText, languages, langManager.getBundle()).showAndWait();
    }

    public Parent getView(){
        return menuBar;
    }
}
