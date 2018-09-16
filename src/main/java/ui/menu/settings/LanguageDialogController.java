package ui.menu.settings;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The controller for the language dialog
 * It lets the user choose the language to be used in the game
 */
public class LanguageDialogController {

    /**
     * The {@link LanguageDialogController} logger
     */
    private static Logger LOGGER = LoggerFactory.getLogger(LanguageDialogController.class);

    /**
     * The dialog's stage
     */
    private Stage dialogStage;

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        LOGGER.info("initialize");
    }

    /**
     * This will let the program know, that the language was changed
     */
    @FXML
    private void handleOk() {
        // TODO: Implement me
        dialogStage.close();
    }

    /**
     * Closes the dialog without any changes
     */
    @FXML
    private void handleCancel() {
        // TODO: Implement me
        dialogStage.close();
    }

    /**
     * Sets the stage of this dialog
     * @param dialogStage the dialog to be set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
