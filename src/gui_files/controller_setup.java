package gui_files;

import enums.OpSystem;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 * 
 * @author David
 */
public class controller_setup implements Initializable {
    
    private sonarqube_helper sqHelper;
    private String[] paths;
    private OpSystem system;
    private String src;
            
    @FXML
    private Label lblHeader, lblSQRoot, lblSQScanner;
    
    @FXML
    private Button btnSQRoot, btnSQScanner, btnSave;
    
    @FXML
    private ChoiceBox cbOS;
    
    @FXML
    private TextField txtSQRoot, txtSQScanner, txtProjectSource;
    
    /**
     * Saves the set root fields after pressing te 'Save' button.
     */
    @FXML
    public void save() {
        if (validateFields()) {
            sqHelper.saveSetup(paths, system, src);
            try {
                sqHelper.openMainScreen();
            } catch (IOException ex) {
                Logger.getLogger(controller_setup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            sqHelper.showWarningDialog("Select both roots before saving");
        }
    }
    
    /**
     * Sets the sonarqube root file
     */
    @FXML
    public void setSQRoot() {
        paths[0] = sqHelper.openFileExplorer("Select the SonarQube root folder");
        
        //Give value to the field in the GUI
        txtSQRoot.setText(paths[0]);
    }
    
    /**
     * Sets the sonartqube scanner file
     */
    @FXML
    public void setSQScanner() {
        paths[1] = sqHelper.openFileExplorer("Select the SonarQube scanner folder");
        
        //Give value to the field in the GUI
        txtSQScanner.setText(paths[1]);
    }
    
    /**
     * Initializes the controller class.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    /**
     * Sets an instance of a sonarqube_helper object.
     * 
     * @param sqHelper 
     */
    public void setSQHelper(sonarqube_helper sqHelper) {
        this.sqHelper = sqHelper;
    }
    
    /**
     * Sets the already existing values of the sonarqube roots.
     * 
     * @param paths 
     * @param system 
     * @param src 
     */
    public void setValues(String[] paths, OpSystem system, String src) {
        this.paths = new String[2];
        this.paths[0] = paths[0];
        this.paths[1] = paths[1];
        this.system = system;
        this.src = src;
        
        fillChoiceBoxOS();
        
        //Give values to the fields in the GUI
        txtSQRoot.setText(paths[0]);
        txtSQScanner.setText(paths[1]);
        txtProjectSource.setText(src);
        setEventHandlers();
    }
    
    /**
     * Sets the items in the choicebox meant for listing operating systems.
     */
    private void fillChoiceBoxOS() {
        int index = 0;
        ArrayList<String> items = new ArrayList();
        for (int i = 0; i < OpSystem.values().length; i++) {
            OpSystem value = OpSystem.values()[i];
            items.add(value.getSystemValue(value));
            if (system != null) {
                if (system.equals(value)) {
                    index = i;
                }
            }
        }
        cbOS.setItems(FXCollections.observableArrayList(items));
        cbOS.getSelectionModel().select(index);
    }
    
    /**
     * Validates the fields.
     * 
     * @return true if both are filled, false if not.
     */
    private boolean validateFields() {
        if (src.isEmpty()) {
            src = "./src";
        }
        return !(txtSQRoot.getText().isEmpty() || txtSQScanner.getText().isEmpty());
    }
    
    /**
     * Sets the eventhandler of the textfields
     */
    private void setEventHandlers() {
        txtSQRoot.textProperty().addListener((observable, oldValue, newValue) -> {
            paths[0] = newValue;
        });
        txtSQScanner.textProperty().addListener((observable, oldValue, newValue) -> {
            paths[1] = newValue;
        });
        txtProjectSource.textProperty().addListener((observable, oldValue, newValue) -> {
            src = newValue;
        });
        cbOS.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov,String old_val, String new_val) {
                    if (new_val != null) {
                        system = OpSystem.getEnumValue(new_val);
                    }
                }
            });
    }
}
