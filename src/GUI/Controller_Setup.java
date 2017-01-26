package GUI;

import java.net.URL;
import java.util.ResourceBundle;
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
    
    @FXML
    private Label lblHeader, lblSQRoot, lblSQScanner;
    
    @FXML
    private Button btnSQRoot, btnSQScanner, btnSave;
    
    @FXML
    private TextField txtSQRoot, txtSQScanner;
    
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
    
    public void setValues(String projectRoot) {
        
    }
}
