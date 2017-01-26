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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author David
 */
public class Controller_Main implements Initializable {

    private SonarQubeHelper sqHelper;
    private String path;
    
    @FXML
    private Label lblHeader, lblProjectRoot;
    
    @FXML
    private Button btnAdd, btnSettings, btnScan;
    
    @FXML
    private TextField txtAdd;
    
    @FXML
    private ProgressBar pbProgress;
    
    @FXML void setProjectRoot() {
        path = sqHelper.openFileExplorer("Select the SonarQube root folder");
        
        //Give value to the field in the GUI
        txtAdd.setText(path);
    }
    
    @FXML
    public void openSetupScreen() {
        try {
            sqHelper.openSetupScreen();
        } catch (IOException ex) {
            Logger.getLogger(Controller_Main.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void setSQHelper(SonarQubeHelper sqHelper) {
        this.sqHelper = sqHelper;
    }
    
    public void setValues(String sqProjectRoot) {
        path = sqProjectRoot;
        
        //Give value to the field in the GUI
        txtAdd.setText(path);
    }
    
    private boolean validateFields() {
        return !path.isEmpty();
    }
}
