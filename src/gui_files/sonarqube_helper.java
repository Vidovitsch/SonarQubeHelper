package gui_files;

import handlers.PropertyHandler;
import models.ScannerTask;
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
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class sonarqube_helper extends Application {
    
    private Stage stage;
    private PropertyHandler pHandler;
    private Thread procedure;
    private Alert info;
    
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

    @Override
    public void stop() {
        //Calling stop instead of interrupt; interrupting won't stop running cmd processes, while stop does.
        procedure.stop();
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
        controller_setup setupController = (controller_setup) loader.getController();
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
        controller_main mainController = (controller_main) loader.getController();
        mainController.setSQHelper(this);
        mainController.setValues(pHandler.getProjectRootFromPropertyFile());
    }
    
    /**
     * Opens the file explorer.
     * 
     * @param title
     * @return The file path of the selected file
     */
    public String openFileExplorer(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File selectedDirectory = chooser.showDialog(stage);
        return selectedDirectory.getPath();
    }
    
    /**
     * Saves the paths set in the setup screen.
     * 
     * @param paths 
     */
    public void saveSetup(String[] paths) {
        try {
            pHandler.savePropertyFileSQRoots(paths);
        } catch (IOException ex) {
            Logger.getLogger(sonarqube_helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Starts the scanning of the set project.
     * First a sonarqube property file is created if it doesn't already exist.
     * 
     * @param path 
     */
    public void startScanning(String path) {
        try {
            pHandler.savePropertyFilePRoot(path);
            if (!pHandler.checkForSQPeropertyFile(path)) {
                pHandler.createSQPropertyFile(path);
            }
            procedure = new Thread(new ScannerTask(this, pHandler.getSQRootsFromPropertyFile()[0],
                    pHandler.getProjectRootFromPropertyFile()));
            procedure.start();
        } catch (IOException ex) {
            Logger.getLogger(sonarqube_helper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Shows a warning dialog with a set header.
     * 
     * @param header 
     */
    public void showWarningDialog(String header) {
        Alert warning = new Alert(AlertType.WARNING);
        warning.setTitle("Warning Dialog");
        warning.setHeaderText(null);
        warning.setContentText(header);
        warning.showAndWait();
    }
    
    /**
     * Shows a error dialog with a set header.
     * 
     * @param header 
     */
    public void showErrorDialog(String header) {
        Alert error = new Alert(AlertType.ERROR);
        error.setTitle("Error Dialog");
        error.setHeaderText(null);
        error.setContentText(header);
        error.showAndWait();
        
        //Interrupts running thread
        procedure.interrupt();
    }
    
    /**
     * Shows a information dialog with a set header.
     * 
     * @param header 
     */
    public void showInfoDialog(String header) {
        info = new Alert(AlertType.INFORMATION);
        info.setTitle("Process Running");
        info.setHeaderText(null);
        info.setContentText(header);
        info.getDialogPane().getChildren().remove(info.getDialogPane().lookupButton(ButtonType.OK));
        info.show();
    }
    
    /**
     * Cancels the shown information dialog.
     */
    public void cancelInfoDialog() {
        info.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
