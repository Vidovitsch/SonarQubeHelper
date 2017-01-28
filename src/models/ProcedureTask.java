package models;

import gui_files.sonarqube_helper;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author David
 */
public class ProcedureTask implements Runnable {

    private String sqRoot;
    private String sqScanner;
    private String projectPath;
    private sonarqube_helper sqHelper;
    
    /**
     * This task runs when the user pressed the 'scan' button.
     * On this thread the selected project gets scanned.
     * 
     * @param sqHelper
     * @param paths
     * @param projectPath 
     */
    public ProcedureTask(sonarqube_helper sqHelper, String[] paths, String projectPath) {
        sqRoot = paths[0];
        sqScanner = paths[1];
        this.projectPath = projectPath;
        this.sqHelper = sqHelper;
    }
    
    @Override
    public void run() {
        if (startSQServer()) {
            if (startSonarScanner()) {
                openBrowser();
            } else {
                cancelInfoDialog();
                showErrorDialog("Scanning failed");
            }
        } else {
            cancelInfoDialog();
            showErrorDialog("The SonarQube server couldn't be started (localhost:9000)");
        }
    }
    
    /**
     * Starts the SonarQube server on localhost:9000.
     * If the server is already online, the command output gives "ERROR",
     * in that case this method will be skipped.
     * 
     * @return true if successful, else false.
     */
    private boolean startSQServer() {
        try {
            //Show dialog to the user
            showInfoDialog("This scan can take several seconds");
            
            //Creating the correct command and executes it 
            Process startServer = Runtime.getRuntime().exec(sqRoot + "\\bin\\windows-x86-64\\StartSonar.bat");
            //startServer.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(startServer.getErrorStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("ERROR")) {
                    break;
                }
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(ProcedureTask.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Starts the scanning of the selected project.
     * 
     * @return 
     */
    private boolean startSonarScanner() {
        try {
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
            Logger.getLogger(ProcedureTask.class.getName()).log(Level.SEVERE, null, ex);
            showErrorDialog("Couldn't scan project");
            return false;
        }
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
     * Opens the supported browser on localhost:9000
     */
    private void openBrowser() {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:9000/"));
            }
            catch (IOException | URISyntaxException ex) {
                Logger.getLogger(ProcedureTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
