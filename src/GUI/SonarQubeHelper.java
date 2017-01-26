package GUI;

import Handlers.PropertyHandler;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author David
 */
public class SonarQubeHelper extends Application {
    
    private Stage stage;
    private PropertyHandler pHandler;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.pHandler = new PropertyHandler();
        
        if (pHandler.checkForPropertyFile()) {
            openMainScreen();
        } else {
            openSetupScreen();
        }
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
        Controller_Setup setupController = (Controller_Setup) loader.getController();
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
        Controller_Main mainController = (Controller_Main) loader.getController();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
