package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
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
    public GridPane menuPane;

    private final PropertiesManager langManager;

    public MenuController(){
        // TODO: Make this injectable...
        this.langManager = new PropertiesManager("properties/lang");
    }

    @FXML
    public void initialize(){
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

        languageChooser.getItems().addAll(languages);
    }

    public Parent getView(){
        return menuPane;
    }
}
