package GUI;

import Handlers.PropertyHandler;
import Models.ProcedureTask;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class SonarQubeHelper extends Application {
    
    private Stage stage;
    private PropertyHandler pHandler;
    private Thread procedure;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.pHandler = new PropertyHandler();
        if (pHandler.checkForPropertyFile()) {
            openMainScreen();
        } else {
            pHandler.createPropertyFile();
            openSetupScreen();
        }
    }

    /**
     * Opens the setup screen.
     * This method only gets called when the button 'settings' has been pressed
     * or when it's the first time this application gets started.
     * 
     * @throws IOException 
     */
    public void openSetupScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLSetup.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Setup");
        stage.show();
        Controller_Setup setupController = (Controller_Setup) loader.getController();
        setupController.setSQHelper(this);
        setupController.setValues(pHandler.getSQRootsFromPropertyFile());
    }
    
    /**
     * Opens the main screen.
     * 
     * @throws IOException 
     */
    public void openMainScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Main");
        stage.show();
        Controller_Main mainController = (Controller_Main) loader.getController();
        mainController.setSQHelper(this);
        mainController.setValues(pHandler.getProjectRootFromPropertyFile());
    }
    
    /**
     * Opens the file explorer.
     * @param title
     * @return The file path of the selected file
     */
    public String openFileExplorer(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File selectedDirectory = chooser.showDialog(stage);
        return selectedDirectory.getPath();
    }
    
    public void saveSetup(String[] paths) {
        try {
            pHandler.savePropertyFileSQRoots(paths);
        } catch (IOException ex) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void startScanning(String path) {
        try {
            pHandler.savePropertyFilePRoot(path);
            if (!pHandler.checkForSQPeropertyFile(path)) {
                pHandler.createSQPropertyFile(path);
            }
            procedure = new Thread(new ProcedureTask(this, pHandler.getSQRootsFromPropertyFile(),
                    pHandler.getProjectRootFromPropertyFile()));
            procedure.start();
        } catch (IOException ex) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showWarningDialog(String header) {
        Alert warning = new Alert(AlertType.WARNING);
        warning.setTitle("Warning Dialog");
        warning.setHeaderText(header);
        warning.setContentText(null);
        warning.showAndWait();
    }
    
    public void showErrorDialog(String header) {
        Alert warning = new Alert(AlertType.ERROR);
        warning.setTitle("Error Dialog");
        warning.setHeaderText(header);
        warning.setContentText(null);
        warning.showAndWait();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
