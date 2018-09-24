package ui.menu;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.dialog.DialogHelper;
import util.properties.PropertiesManager;

import java.util.List;
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
     * The language choose prompt
     */
    @FXML
    public MenuItem menuItemLanguage;

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize(){
        LOGGER.info("Initialize");

        // Set the click listener for the 'Choose language' menu
        menuItemLanguage.setOnAction(event -> showLanguageDialog());

        // Add language change listener
        addLanguageChangeListener(menuSettings, "Menu.Settings");
        PropertiesManager.addSubscriber(menuSettings.textProperty());

        addLanguageChangeListener(menuItemLanguage, "Dialog.Title.Language");
        PropertiesManager.addSubscriber(menuItemLanguage.textProperty());
    }

    /**
     * Opens a dialog for choosing a language, which will be used throughout the application
     */
    private void showLanguageDialog(){

        LOGGER.info("Show language dialog");

        String languageText = "Language";

        // Get all available languages
        List<String> languages = PropertiesManager.getAllStrings(languageText, "settings");

        // Wait until the user has chosen a language
        Optional<String> result = DialogHelper.createLanguageChoiceDialog(languageText, languages, PropertiesManager.getBundle("lang")).showAndWait();

        // Change the language
        result.ifPresent(PropertiesManager::changeLanguage);
    }

    /**
     * Changes the language of a menu item
     * @param item the menu item to be changed
     * @param key the key, which is used for finding the item's value
     */
    private void addLanguageChangeListener(MenuItem item, String key) {
        item.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        item.textProperty().setValue(PropertiesManager.getString(key, "lang"))
        );
    }

    /**
     * Get the menu bar as a parent of the fxml file
     * @return menuBar
     */
    public Parent getView(){
        return menuBar;
    }
}
