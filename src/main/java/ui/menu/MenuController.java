package ui.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import util.properties.PropertiesManager;

/**
 * The controller, which handles the top menu actions
 */
public class MenuController {

    /**
     * The {@link ComboBox} with all supported languages
     */
    @FXML
    public ComboBox<String> languageChooser;

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

    private PropertiesManager langManager;

    @FXML
    public void initialize(){
        this.langManager = new PropertiesManager("properties/lang");
        initLanguageSupport(langManager);
    }

    /**
     * Initializes the language support
     * It takes the supported locales and displays them in the combo box
     * @param langManager the manager containing all values
     */
    private void initLanguageSupport(PropertiesManager langManager){

        ObservableList<String> languages = FXCollections.observableArrayList();

        langManager.getSupportedLocales().forEach(locale -> {
            // Map the languages manually
            // TODO: Isn't there a way to do that automatically?
            switch (locale.getLanguage()){
                case "de":
                    languages.add(langManager.getString("Language.German"));
                    break;
                case "en":
                    languages.add(langManager.getString("Language.English"));
                    break;
                case "es":
                    languages.add(langManager.getString("Language.Spanish"));
                    break;
                case "ru":
                    languages.add(langManager.getString("Language.Russian"));
                    break;
            }
        });

        //languageChooser.setPromptText(langManager.getString("Language.PromptText"));

        //languageChooser.setItems(languages);
        //languageChooser.setValue(languageChooser.getItems().get(0));
    }

    public Parent getView(){
        return menuBar;
    }
}
