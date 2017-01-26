package gui_files;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author David
 */
public class controller_main implements Initializable {

    private sonarqube_helper sqHelper;
    private String path;
    
    @FXML
    private Label lblHeader, lblProjectRoot;
    
    @FXML
    private Button btnAdd, btnSettings, btnScan;
    
    @FXML
    private TextField txtAdd;
    
    /**
     * Starts the scanner after validating the fields.
     */
    @FXML
    public void start() {
        if (validateFields()) {
            sqHelper.startScanning(path);
        } else {
            sqHelper.showWarningDialog("Select a project root before scanning");
        }
    }
    
    /**
     * Sets the set path, chosen from the file explorer.
     */
    @FXML
    public void setProjectRoot() {
        path = sqHelper.openFileExplorer("Select your project's root folder");
        
        //Give value to the field in the GUI
        txtAdd.setText(path);
    }
    
    /**
     * Opens the setup-screen.
     */
    @FXML
    public void openSetupScreen() {
        try {
            sqHelper.openSetupScreen();
        } catch (IOException ex) {
            Logger.getLogger(controller_main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * Sets the already existing value of the project root.
     * 
     * @param sqProjectRoot
     */
    public void setValues(String sqProjectRoot) {
        path = sqProjectRoot;
        
        //Give value to the field in the GUI
        txtAdd.setText(path);
        setEventHandlers();
    }
    
    /**
     * Validates the fields.
     * 
     * @return true if the field is filled, false if not.
     */
    private boolean validateFields() {
        return !txtAdd.getText().isEmpty();
    }
    
    /**
     * Sets the eventhandler of the textfields
     */
    private void setEventHandlers() {
        txtAdd.textProperty().addListener((observable, oldValue, newValue) -> {
            path = newValue;
        });
    }
}
