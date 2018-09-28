package util.dialog;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.properties.PropertiesManager;

import java.util.List;
import java.util.ResourceBundle;

/**
 * A dialog helper
 */
public class DialogHelper {

    /**
     * The {@link DialogHelper} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogHelper.class);

    // static class
    private DialogHelper(){}

    /**
     * Creates an dialog dialog with the given dialog text
     * @param errorText the dialog text to be displayed
     */
    public static void createErrorDialog(String errorText){
        LOGGER.debug("Enter createErrorDialog");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setContentText(errorText);
        alert.show();
    }

    /**
     * Creates an information dialog with the given information text
     * @param text the information text to be displayed
     * @return the information dialog
     */
    public static Alert createInformationDialog(String text) {
        LOGGER.debug("Enter createInformationDialog");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.setContentText(text);
        return alert;
    }

    /**
     * Creates a confirmation dialog with the given confirmation text
     * @param text the information text to be displayed
     * @return the confirmation dialog
     */
    public static Alert createConfirmationDialog(String text) {
        LOGGER.debug("Enter createConfirmationDialog");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(text);
        return alert;
    }

    /**
     * Creates a custom confirmation dialog with the given text and several buttons
     * @param text the text to be displayed
     * @param buttonTypes a variable argument list of buttons
     * @return the confirmation dialog
     */
    public static Alert createCustomConfirmationDialog(String text, ButtonType... buttonTypes) {
        LOGGER.debug("Enter createCustomConfirmationDialog");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(text);

        // Set all buttons at once
        alert.getButtonTypes().setAll(buttonTypes);

        return alert;
    }

    /**
     * Creates a choice dialog
     * @param choiceText the key, which will be used for searching the dialog text
     * @param activeItem the active item
     * @param choices the list of different choices
     * @param bundle the bundle to be used
     * @return a choice dialog
     */
    private static ChoiceDialog<String> createChoiceDialog(String choiceText, String activeItem, List<String> choices, ResourceBundle bundle) {
        LOGGER.debug("Enter createChoiceDialog");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(activeItem, choices);

        dialog.setTitle(bundle.getString("Dialog.Title." + choiceText));
        dialog.setHeaderText(bundle.getString("Dialog.Header." + choiceText));
        dialog.setContentText(bundle.getString("Dialog.Content." + choiceText) + ": ");

        return dialog;
    }

    /**
     * Creates a language choice dialog with the current language as the active item
     * @param choiceText the key, which will be used for searching the dialog text
     * @param choices the list of different choices
     * @param bundle the bundle to be used
     * @return a choice dialog
     */
    public static ChoiceDialog<String> createLanguageChoiceDialog(String choiceText, List<String> choices, ResourceBundle bundle) {
        return createChoiceDialog(choiceText, PropertiesManager.getCurrentLanguage(), choices, bundle);
    }

    /**
     * Creates a simple dialog stage with the given information
     * @param title the title of the dialog
     * @param owner the owner stage of this dialog stage
     * @param pane the root of the fxml file
     * @return the dialog stage itself
     */
    public static Stage createDialogStage(String title, Stage owner, Pane pane) {
        LOGGER.debug("Enter createDialogStage");

        // Create the new dialog stage
        Stage dialogStage = new Stage();

        // Set the stage's title
        dialogStage.setTitle(title);

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(owner);
        Scene scene = new Scene(pane);
        dialogStage.setScene(scene);

        return dialogStage;
    }

}
