package guifiles;

import enums.opsystem;
import handlers.PropertyHandler;
import models.ScannerTask;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.ServerTask;

/**
 *
 * @author David
 */
public class SonarQubeHelper extends Application {
    
    private Stage stage;
    private ControllerMain mainController;
    private PropertyHandler pHandler;
    private Thread procedure;
    private Alert info;
    private static final int PORTNUMBER = 9000;
    
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
        procedure.interrupt();
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
        ControllerSetup setupController = (ControllerSetup) loader.getController();
        setupController.setSQHelper(this);
        setupController.setValues(pHandler.getSQRootsFromPropertyFile(), pHandler.getSystemFromPropertyFile(),
                pHandler.getSourceFromPropertyFile());
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
        mainController = (ControllerMain) loader.getController();
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
     * @param system 
     * @param src 
     */
    public void saveSetup(String[] paths, opsystem system, String src) {
        try {
            pHandler.savePropertyFileSQRoots(paths);
            pHandler.savePropertyFileSystem(system);
            pHandler.savePropertyFileSource(src);
        } catch (IOException ex) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
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
            pHandler.savePropertyFileRoot(path);
            pHandler.createSQPropertyFile(path);
            if (checkPortAvailable()) {
                (new Thread(new ServerTask(this, pHandler.getSQRootsFromPropertyFile()[0], path,
                        pHandler.getSystemFromPropertyFile()))).start();
            }
            else {
                procedure = new Thread(new ScannerTask(this, pHandler.getSQRootsFromPropertyFile()[1],
                        pHandler.getProjectRootFromPropertyFile()));
                procedure.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
            showErrorDialog("Can't scan this project");
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
        Platform.runLater(() -> {
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Error Dialog");
            error.setHeaderText(null);
            error.setContentText(header);
            error.showAndWait();
            
            //Interrupts running thread
            procedure.interrupt();
        });
    }
    
    /**
     * Shows a information dialog with a set header.
     * 
     * @param header 
     * @param message 
     */
    public void showInfoDialog(String header, String message) {
        Platform.runLater(() -> {
            info = new Alert(AlertType.INFORMATION);
            info.setTitle("Process Running");
            info.setHeaderText(header);
            info.setContentText(message);
            info.getDialogPane().getChildren().remove(info.getDialogPane().lookupButton(ButtonType.OK));
            info.show();
        });
    }
    
    /**
     * Cancels the shown information dialog.
     */
    public void cancelInfoDialog() {
        Platform.runLater(info::close);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
    * Checks to see if a specific port is available.
    *
    * @return 
    */
    public boolean checkPortAvailable() {
        try (ServerSocket socket = new ServerSocket(PORTNUMBER)) {
            socket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }
    
    /**
     * Gets the properies of previous scanned projects.
     * 
     * @return Properties
     */
    public Properties getPrevProjectProperties() {
        try {
            return pHandler.getPrevProjectProperties();
        } catch (IOException ex) {
            Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Won't happen
        return null;
    }
    
    /**
     * Creates a new fast-scan project after scanned successfully.
     * 
     * @param path 
     */
    public void setNewPrevProject(String path) {
        Platform.runLater(() -> {
            try {
                pHandler.setNewPrevProject(path);
                mainController.setChoiceBoxModel(getPrevProjectProperties());
            } catch (IOException ex) {
                Logger.getLogger(SonarQubeHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * Lets the method 'updateServerConnection' (in ControllerMain) run on the JavaFX-thread
     */
    public void setServerConnectionOn() {
        Platform.runLater(() -> mainController.updateServerConnection(true));
    }
}
