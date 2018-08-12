package ui.game;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class InformationController {

    @FXML
    public Label lbl_armies;

    @FXML
    public Label lbl_phase;

    @FXML
    public AnchorPane informationPane;
    public HBox informationBox;

    @FXML
    public void initialize(){

    }

    public Label getLabelArmies() {
        return lbl_armies;
    }

    public void setLabelArmies(String armies) {
        lbl_armies.setText(armies);
    }

    public Label getLabelPhase() {
        return lbl_phase;
    }

    public void setLabelPhase(String phase) {
        lbl_phase.setText(phase);
    }

    public StringProperty armiesTextProperty(){
        return lbl_armies.textProperty();
    }

    public StringProperty phaseTextProperty(){
        return lbl_phase.textProperty();
    }

    public Parent getView(){
        return informationPane;
    }
}
