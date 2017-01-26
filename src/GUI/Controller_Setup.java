package GUI;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

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
    public void setSQRoot() {
        String path = sqHelper.openFileExplorer("Select the SonarQube root folder");
        paths[0] = path;
        
        //Give value to the field in the GUI
        txtSQRoot.setText(path);
    }
    
    @FXML
    public void setSQScanner() {
        String path = sqHelper.openFileExplorer("Select the SonarQube scanner folder");
        paths[1] = path;
        
        //Give value to the field in the GUI
        txtSQScanner.setText(path);
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
    
    public void setValues(String sqRoot, String sqScanner) {
        this.paths = new String[2];
        paths[0] = sqRoot;
        paths[1] = sqScanner;
        
        //Give values to the fields in the GUI
        txtSQRoot.setText(sqRoot);
        txtSQScanner.setText(sqScanner);
    }
}
