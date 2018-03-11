package util.error;

import javafx.scene.control.Alert;

/**
 * A simple error dialog
 */
public class ErrorDialog {

    // static class
    private ErrorDialog(){}

    /**
     * Creates an error dialog with the given error text
     * @param errorText the error text to be displayed
     */
    public static void showErrorDialog(String errorText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setContentText(errorText);
        alert.show();
    }
}
