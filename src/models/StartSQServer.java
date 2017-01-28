package models;

import gui_files.sonarqube_helper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author David
 */
public class StartSQServer implements Runnable {

    private sonarqube_helper sqHelper;
    private String sqRoot;
    
    public  StartSQServer(sonarqube_helper sqHelper, String sqRoot) {
        this.sqRoot = sqRoot;
        this.sqHelper = sqHelper;
    }
    
    @Override
    public void run() {
       
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
            Logger.getLogger(ScannerTask.class.getName()).log(Level.SEVERE, null, ex);
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
}
