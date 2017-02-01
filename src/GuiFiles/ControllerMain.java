package GuiFiles;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author David
 */
public class ControllerMain implements Initializable {

    private SonarQubeHelper sqHelper;
    private String path;
    private boolean connected = false;
    
    @FXML
    private ChoiceBox cbProjects;
    
    @FXML
    private Label lblServerConnection, lblHeader, lblProjectRoot;
    
    @FXML
    private Button btnAdd, btnSettings, btnScan;
    
    @FXML
    private TextField txtAdd;
    
    @FXML
    private Circle circleServerConnection;
    
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
            Logger.getLogger(ControllerMain.class.getName()).log(Level.SEVERE, null, ex);
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
     * Sets an instance of a SonarQubeHelper object.
     * 
     * @param sqHelper 
     */
    public void setSQHelper(SonarQubeHelper sqHelper) {
        this.sqHelper = sqHelper;
    }
    
    /**
     * Sets the already existing value of the project root.
     * 
     * @param sqProjectRoot
     */
    public void setValues(String sqProjectRoot) {
        if (!sqProjectRoot.isEmpty()) {
            path = sqProjectRoot;
        }
        
        //Set previous projectnames in the choice box
        Properties projects = sqHelper.getPrevProjectProperties();
        setChoiceBoxModel(projects);
        
        //Give value to the field in the GUI
        txtAdd.setText(path);
        setEventHandlers();
        
        //Update the server connection status visually in the GUI
        updateServerConnection(!sqHelper.checkPortAvailable());
    }
    
    /**
     * Sets all the keys from the properties in a choice box.
     * According to the last used project path, a selection is made from
     * one of the choicebox items.
     * @param props 
     */
    public void setChoiceBoxModel(Properties props) {
        int index = -1;
        int indexCounter = 0;
        ArrayList<String> items = new ArrayList();
        Enumeration e = props.propertyNames();
        
        //All propertykeys are added to a temp list as String values
        //While this happens, a counter is running for each iteration.
        //When the project path is the same as the property value of iterated key,
        //the current index is saved.
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            items.add(key);
            if (!path.isEmpty()) {
                if (path.equals(props.getProperty(key))) {
                    index = indexCounter;
                }
            }
            indexCounter++;
        }
        
        //Set the choicebox items
        cbProjects.setItems(FXCollections.observableArrayList(items));
        
        //Set the selected item with the saved index
        if (index != -1) {
            cbProjects.getSelectionModel().select(index);
        }
    }
    
    /**
     * Updates the GUI.
     * If there is a connection with the SonarQube server, it turns green with a corresponding text.
     * If there is no connection with te SonarQube server, it turns red with a corresponding text.
     * @param c 
     */
    public void updateServerConnection(boolean c) {
        if (connected != c) {
            if (c) {
                lblServerConnection.setText("Connected to server");
                circleServerConnection.setFill(Color.web("#85BB43"));
            } else {
                lblServerConnection.setText("No server connection");
                circleServerConnection.setFill(Color.web("#D8ACE1"));
            }
            connected = c;
        }
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
        cbProjects.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov,String old_val, String new_val) {
                    if (new_val != null) {
                        txtAdd.setText(sqHelper.getPrevProjectProperties().getProperty(new_val));
                    }
                }
            });
    }
}
