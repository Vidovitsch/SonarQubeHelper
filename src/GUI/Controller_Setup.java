package GUI;

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
public class Controller_Setup implements Initializable {
    
    private SonarQubeHelper sqHelper;
    private String[] paths;
            
    @FXML
    private Label lblHeader, lblSQRoot, lblSQScanner;
    
    @FXML
    private Button btnSQRoot, btnSQScanner, btnSave;
    
    @FXML
    private TextField txtSQRoot, txtSQScanner;
    
    @FXML
    public void save() {
        if (validateFields()) {
            sqHelper.saveSetup(paths);
            try {
                sqHelper.openMainScreen();
            } catch (IOException ex) {
                Logger.getLogger(Controller_Setup.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            sqHelper.showWarningDialog("Select both roots before saving");
        }
    }
    
    @FXML
    public void setSQRoot() {
        paths[0] = sqHelper.openFileExplorer("Select the SonarQube root folder");
        
        //Give value to the field in the GUI
        txtSQRoot.setText(paths[0]);
    }
    
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
    
    public void setSQHelper(SonarQubeHelper sqHelper) {
        this.sqHelper = sqHelper;
    }
    
    public void setValues(String[] paths) {
        this.paths = new String[2];
        this.paths[0] = paths[0];
        this.paths[1] = paths[1];
        
        //Give values to the fields in the GUI
        txtSQRoot.setText(paths[0]);
        txtSQScanner.setText(paths[1]);
        setEventHandlers();
    }
    
    private boolean validateFields() {
        return !(txtSQRoot.getText().isEmpty() || txtSQScanner.getText().isEmpty());
    }
    
    private void setEventHandlers() {
        txtSQRoot.textProperty().addListener((observable, oldValue, newValue) -> {
            paths[0] = newValue;
        });
        txtSQScanner.textProperty().addListener((observable, oldValue, newValue) -> {
            paths[1] = newValue;
        });
    }
}
