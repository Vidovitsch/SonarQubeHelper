package Models;

import GUI.SonarQubeHelper;
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
public class ProcedureTask extends Task {

    private String sqRoot;
    private String sqScanner;
    private String projectPath;
    private SonarQubeHelper sqHelper;
    
    public ProcedureTask(SonarQubeHelper sqHelper, String[] paths, String projectPath) {
        sqRoot = paths[0];
        sqScanner = paths[1];
        this.projectPath = projectPath;
        this.sqHelper = sqHelper;
    }
    
    @Override
    protected Object call() throws Exception {
        startSQServer();
        startSonarScanner();
        return null;
    }
    
    private boolean startSQServer() {
        try {
            Process startServer = Runtime.getRuntime().exec(sqRoot + "\\bin\\windows-x86-64\\StartSonar.bat");
            Scanner scanner = new Scanner(startServer.getInputStream());
            while (scanner.hasNext()) {
                if (scanner.next().contains("ERROR")) break;
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ProcedureTask.class.getName()).log(Level.SEVERE, null, ex);
            showErrorDialog("The SonarQube server couldn't be started (localhost:9000)");
            return false;
        }
    }
    
    private boolean startSonarScanner() {
        try {
            showInfoDialog("This scan can take several seconds");
            
            String command = "cd " + projectPath + " && " + sqScanner + "\\bin\\sonar-scanner";
            ProcessBuilder pBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Process scanProject = pBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(scanProject.getInputStream()));
            
            String l;
            while ((l = reader.readLine()) != null) { }
            cancelInfoDialog();
            
            openBrowser();
            return true;
        }
        catch (IOException ex) {
            Logger.getLogger(ProcedureTask.class.getName()).log(Level.SEVERE, null, ex);
            showErrorDialog("Couldn't scan project");
            return false;
        }
    }
    
    private void showErrorDialog(String header) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.showErrorDialog(header);
            }
        });
    }
    
    private void showInfoDialog(String header) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.showInfoDialog(header);
            }
        });
    }
    
    private void cancelInfoDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                sqHelper.cancelInfoDialog();
            }
        });
    }
    
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
