package models;

import enums.opsystem;
import guifiles.SonarQubeHelper;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ServerTask implements Runnable {

    private SonarQubeHelper sqHelper;
    private String sqRoot;
    private String projectRoot;
    private opsystem system;
    
    /**
     * A Runnable for starting the SonarQube server.
     * 
     * @param sqHelper
     * @param sqRoot
     * @param projectRoot
     * @param system 
     */
    public ServerTask(SonarQubeHelper sqHelper, String sqRoot, String projectRoot, opsystem system) {
        this.sqHelper = sqHelper;
        this.sqRoot = sqRoot;
        this.projectRoot = projectRoot;
        this.system = system;
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
            sqHelper.showInfoDialog("Starting SonarQube server", "This can take up to a minute");
            
            //Creating the correct command and executes it 
            Runtime.getRuntime().exec(sqRoot + "\\bin" + system.getFilePath(system));

            return true;
        } catch (IOException ex) {
            Logger.getLogger(ScannerTask.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Starts timer that checks the server connectivity every second.
     */
    private void startConnectionCheck() {
        Timer connectionTimer = new Timer();
        connectionTimer.schedule(new ConnectionTask(connectionTimer), 0, 1000);
    }
    
    /**
     * A task that checks the server connectivity.
     * If a connection is no longer available, the SonarQube scanner is activated.
     */
    private class ConnectionTask extends TimerTask {
        private Timer timer;
        
        public ConnectionTask(Timer timer) {
            this.timer = timer;
        }
        
        @Override
        public void run() {
            if(!sqHelper.checkPortAvailable()) {
                sqHelper.cancelInfoDialog();
                sqHelper.startScanning(projectRoot);
                sqHelper.setServerConnectionOn();
                timer.cancel();
            }
        }
    }
}
