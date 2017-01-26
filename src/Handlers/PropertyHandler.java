package Handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author David
 */
public class PropertyHandler {

    private final String propertySQFileName = "sonar-project.properties";
    private final String propertyFileName = "sq_helper.properties";
    private final String rootKey = "sqRoot";
    private final String scannerKey = "sqScanner";
    private final String projectKey = "pRoot";
    
    /**
     * Checks if the property file 'sq_helper.properties' already exists.
     * 
     * @return true if it exists, else false
     * @throws java.io.IOException
     */
    public boolean checkForPropertyFile() throws IOException {
        File propertyFile = new File("sq_helper.properties");
        return propertyFile.exists();
    }
    
    /**
     * Gets the two set roots from the property file. (SonarQube, SonarQubeScanner)
     * If the property file doesn't exist, returns empty strings.
     * 
     * @return an array of two roots.
     * @throws IOException 
     */
    public String[] getSQRootsFromPropertyFile() throws IOException {
        String[] paths = new String[2];
        Properties sq_helper = new Properties();

        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
            paths[0] = sq_helper.getProperty(rootKey);
            paths[1] = sq_helper.getProperty(scannerKey);
        }
        return paths;
    }
    
    /**
     * Gets the project root form the property file.
     * If the property file doen't contain a projectname property, it returns an empty string.
     * 
     * @return path of the project root.
     * @throws IOException 
     */
    public String getProjectRootFromPropertyFile() throws IOException {
        String path = "";
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
            path = sq_helper.getProperty(projectKey);
        }
        return path;
    }
    
    /**
     * Saves the new SQroot paths to the property file
     * 
     * @param paths
     * @throws IOException 
     */
    public void savePropertyFileSQRoots(String[] paths) throws IOException {
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            sq_helper.replace(rootKey, paths[0]);
            sq_helper.replace(scannerKey, paths[1]);
            sq_helper.store(output, null);
        }
    }
    
    /**
     * Saves the new project path to the property file
     * 
     * @param path 
     * @throws java.io.IOException 
     */
    public void savePropertyFilePRoot(String path) throws IOException {
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            sq_helper.replace(projectKey, path);
            sq_helper.store(output, null);
        }
    }
    
    public void createSQPropertyFile(String path) throws IOException {
        String projectName = getProjectName(path);
        try (FileOutputStream output = new FileOutputStream(path + "\\" + propertySQFileName)) {
            Properties sonar_project = new Properties();
            sonar_project.setProperty("sonar.projectKey", projectName);
            sonar_project.setProperty("sonar.projectName", projectName);
            sonar_project.setProperty("sonar.projectVersion", "1.0");
            sonar_project.setProperty("sonar.sources", "./src");
            sonar_project.store(output, null);
        }
    }
    
    public void createPropertyFile() throws IOException {
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            Properties sq_helper = new Properties();
            sq_helper.setProperty(rootKey, "");
            sq_helper.setProperty(scannerKey, "");
            sq_helper.setProperty(projectKey, "");
            sq_helper.store(output, null);
        }
    }
    
    public boolean checkForSQPeropertyFile(String path) {
        File propertyFile = new File(path + "\\" + propertySQFileName);
        return propertyFile.exists();
    }
    
    private String getProjectName(String path) {
        String newPath = path.replace("\\", ";");
        String[] values = newPath.split(";");
        int maxIndex = values.length - 1;
        return values[maxIndex];
    }
}
