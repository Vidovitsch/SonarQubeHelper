package models;

import gui_files.sonarqube_helper;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
    private final static String batchFile = "startSQServer.bat";
    
    public  StartSQServer(sonarqube_helper sqHelper, String sqRoot) {
        this.sqHelper = sqHelper;
        this.sqRoot = sqRoot;
    }
    
    @Override
    public void run() {
        startConnectionCheck();
        startSQServer(sqRoot);
    }
    
    /**
     * Starts the SonarQube server on localhost:9000.
     * If the server is already online, the command output gives "ERROR",
     * in that case this method will be skipped.
     * 
     * @return true if successful, else false.
     */
    private boolean startSQServer(String sqRoot) {
        try {
            //Show dialog to the user
            showInfoDialog("Starting SonarQube server: This can take a while");
            
            //Creating the correct command and executes it 
            Runtime.getRuntime().exec(sqRoot + "\\bin\\windows-x86-64\\StartSonar.bat");

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
    
    private void startConnectionCheck() {
        System.out.println("Starting connection timer");
        Timer connectionTimer = new Timer();
        connectionTimer.schedule(new connectionTask(connectionTimer), 0, 1000);
    }
    
    private class connectionTask extends TimerTask {
        private Timer timer;
        
        public connectionTask(Timer timer) {
            this.timer = timer;
        }
        
        @Override
        public void run() {
            if(!sqHelper.checkPortAvailable()) {
                timer.cancel();
                cancelInfoDialog();
                showInfoDialog("SonarQube server started!");
            }
        }
    }
}
