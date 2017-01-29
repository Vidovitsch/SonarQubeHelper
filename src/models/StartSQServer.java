package models;

import gui_files.sonarqube_helper;
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
    private String projectRoot;
    
    public  StartSQServer(sonarqube_helper sqHelper, String sqRoot, String projectRoot) {
        this.sqHelper = sqHelper;
        this.sqRoot = sqRoot;
        this.projectRoot = projectRoot;
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
                cancelInfoDialog();
                sqHelper.startScanning(projectRoot);
                timer.cancel();
            }
        }
    }
}
