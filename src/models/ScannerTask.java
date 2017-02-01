package models;

import guiFiles.sonarqubeHelper;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ScannerTask implements Runnable {

    private String sqScanner;
    private String projectPath;
    private sonarqubeHelper sqHelper;
    
    /**
     * This task runs when the user pressed the 'scan' button.
     * On this thread the selected project gets scanned.
     * 
     * @param sqHelper
     * @param sqScanner
     * @param projectPath 
     */
    public ScannerTask(sonarqubeHelper sqHelper, String sqScanner, String projectPath) {
        this.sqScanner = sqScanner;
        this.projectPath = projectPath;
        this.sqHelper = sqHelper;
    }
    
    @Override
    public void run() {
        if (startSonarScanner()) {
            openBrowser();
        } else {
            sqHelper.cancelInfoDialog();
            sqHelper.showErrorDialog("Scanning failed");
        }
    }

    /**
     * Starts the scanning of the selected project.
     * 
     * @return 
     */
    private boolean startSonarScanner() {
        try {
            int infoCounter = 0;
            
            sqHelper.showInfoDialog("Scanning project", "This can take up to a minute (depends on project size)");
            
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
                } else if (line.contains("INFO") && infoCounter == 0) {
                    infoCounter++;
                }
            }
            
            //Check the amount of lines containing 'INFO'.
            //If its 0 means that there has been an exception thrown while sanning the project
            if (infoCounter == 0) {
                return false;
            }
            
            //Set new previous scanned project
            
            sqHelper.setNewPrevProject(projectPath);
            
            //Cancels the dialog shown
            sqHelper.cancelInfoDialog();
            return true;
        }
        catch (IOException ex) {
            Logger.getLogger(ScannerTask.class.getName()).log(Level.SEVERE, null, ex);
            sqHelper.showErrorDialog("Couldn't scan project");
            return false;
        }
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
