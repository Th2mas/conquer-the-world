package ui.game;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The controller for the information bar
 */
public class InformationController {

    /**
     * The {@link InformationController} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InformationController.class);

    /**
     * The label for the armies
     */
    @FXML
    public Label lbl_armies;

    /**
     * The label for the phase
     */
    @FXML
    public Label lbl_phase;

    /**
     * The main pane
     */
    @FXML
    public AnchorPane informationPane;

    /**
     * The box containing the elements
     */
    @FXML
    public HBox informationBox;

    /**
     * Initialize the InformationController
     */
    @FXML
    public void initialize(){
        LOGGER.info("Initialize");
    }

    /**
     * Get the armies property
     * @return armies text property
     */
    public StringProperty armiesTextProperty(){
        return lbl_armies.textProperty();
    }

    /**
     * Get the phase property
     * @return phase text property
     */
    public StringProperty phaseTextProperty(){
        return lbl_phase.textProperty();
    }

    /**
     * Get the information pane as a parent of the fxml file
     * @return informationPane
     */
    public Parent getView(){
        return informationPane;
    }
}
