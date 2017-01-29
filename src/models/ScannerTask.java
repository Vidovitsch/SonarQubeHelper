package models;

import gui_files.sonarqube_helper;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author David
 */
public class ScannerTask implements Runnable {

    private String sqScanner;
    private String projectPath;
    private sonarqube_helper sqHelper;
    
    /**
     * This task runs when the user pressed the 'scan' button.
     * On this thread the selected project gets scanned.
     * 
     * @param sqHelper
     * @param sqScanner
     * @param projectPath 
     */
    public ScannerTask(sonarqube_helper sqHelper, String sqScanner, String projectPath) {
        this.sqScanner = sqScanner;
        this.projectPath = projectPath;
        this.sqHelper = sqHelper;
    }
    
    @Override
    public void run() {
        if (startSonarScanner()) {
            openBrowser();
        } else {
            cancelInfoDialog();
            showErrorDialog("Scanning failed");
        }
    }

    /**
     * Starts the scanning of the selected project.
     * 
     * @return 
     */
    private boolean startSonarScanner() {
        try {
            showInfoDialog("Scanning project: This can take several seconds");
            
            //Creating the correct command and executes it
            String command = "cd " + projectPath + " && " + sqScanner + "\\bin\\sonar-scanner";
            ProcessBuilder pBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process scanProject = pBuilder.start();
            
            //Creating a reader to make sure the command is executed properly before proceding
            BufferedReader reader = new BufferedReader(new InputStreamReader(scanProject.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("FAILURE")) {
                    return false;
                }
            }
            
            //Cancels the dialog shown
            cancelInfoDialog();
            return true;
        }
        catch (IOException ex) {
            Logger.getLogger(ScannerTask.class.getName()).log(Level.SEVERE, null, ex);
            showErrorDialog("Couldn't scan project");
            return false;
        }
    }
    
    /**
     * Shows on the JavaFX thread an information dialog
     * 
     * @param header 
     */
    private void showInfoDialog(String header) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.showInfoDialog(header);
            }
        });
    }
    
    /**
     * Cancels the shown information dialog on the JavaFX thread
     */
    private void cancelInfoDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.cancelInfoDialog();
            }
        });
    }
    
    /**
     * Shows on the JavaFX thread an error dialog
     * 
     * @param header 
     */
    private void showErrorDialog(String header) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.showErrorDialog(header);
            }
        });
    }
    
    /**
     * Opens the supported browser on localhost:9000
     */
    private void openBrowser() {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:9000/"));
            }
            catch (IOException | URISyntaxException ex) {
                Logger.getLogger(ScannerTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}