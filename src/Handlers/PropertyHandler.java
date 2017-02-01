package Handlers;

import Enums.opsystem;
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

    private final static String propertyPrevProjects = "prev_projects.properties";
    private final static String propertySQFileName = "sonar-project.properties";
    private final static String propertyFileName = "sq_helper.properties";
    private final static String rootKey = "sqRoot";
    private final static String scannerKey = "sqScanner";
    private final static String systemKey = "sqSystem";
    private final static String projectKey = "pRoot";
    private final static String sourceKey = "pSource";
    
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
     * Gets the chosen operating system set in the settings.
     * 
     * @return opsystem
     * @throws IOException 
     */
    public opsystem getSystemFromPropertyFile() throws IOException {
        opsystem system = null;
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
            system = opsystem.getEnumValue(sq_helper.getProperty(systemKey));
        }
        return system;
    }
    
    /**
     * Gets the default project source path set in the settings.
     * 
     * @return Source path (String)
     * @throws IOException 
     */
    public String getSourceFromPropertyFile() throws IOException {
        String src;
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
            src = sq_helper.getProperty(sourceKey);
        }
        return src;
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
     * Saves the operating system set in the settings.
     * 
     * @param system
     * @throws IOException 
     */
    public void savePropertyFileSystem(opsystem system) throws IOException {
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            sq_helper.replace(systemKey, system.getSystemValue(system));
            sq_helper.store(output, null);
        }
    }
    
    /**
     * Saves the default project resource path set in the settings.
     * 
     * @param src
     * @throws IOException 
     */
    public void savePropertyFileSource(String src) throws IOException {
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            sq_helper.replace(sourceKey, src);
            sq_helper.store(output, null);
        }
    }
    
    /**
     * Saves the new project path to the property file
     * 
     * @param path 
     * @throws java.io.IOException 
     */
    public void savePropertyFileRoot(String path) throws IOException {
        Properties sq_helper = new Properties();
        try (FileInputStream input = new FileInputStream(propertyFileName)) {
            sq_helper.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            sq_helper.replace(projectKey, path);
            sq_helper.store(output, null);
        }
    }
    
    /**
     * Creates the sonarqube property file for the selected project path.
     * Key = project name.
     * Name = project name.
     * Version = 1.0.
     * Sources = ./src.
     * 
     * @param path
     * @throws IOException 
     */
    public void createSQPropertyFile(String path) throws IOException {
        String projectName = getProjectName(path);
        try (FileOutputStream output = new FileOutputStream(path + "\\" + propertySQFileName)) {
            Properties sonar_project = new Properties();
            sonar_project.setProperty("sonar.projectKey", projectName);
            sonar_project.setProperty("sonar.projectName", projectName);
            sonar_project.setProperty("sonar.projectVersion", "1.0");
            sonar_project.setProperty("sonar.sources", getSourceFromPropertyFile());
            sonar_project.store(output, null);
        }
    }
    
    /**
     * Creates the property file with all the selected roots.
     * All values are created empty.
     * 
     * @throws IOException 
     */
    public void createPropertyFile() throws IOException {
        try (FileOutputStream output = new FileOutputStream(propertyFileName)) {
            Properties sq_helper = new Properties();
            sq_helper.setProperty(rootKey, "");
            sq_helper.setProperty(scannerKey, "");
            sq_helper.setProperty(systemKey, "");
            sq_helper.setProperty(projectKey, "");
            sq_helper.setProperty(sourceKey, "./src");
            sq_helper.store(output, null);
        }
    }
    
    /**
     * Gets the properties of previous scanned projects.
     * 
     * @return Properties
     * @throws IOException 
     */
    public Properties getPrevProjectProperties() throws IOException {
        //Checking if file exists; if not, make a new one
        checkForPrevProjectFile();
        
        Properties prevProjects = new Properties();
        try (FileInputStream input = new FileInputStream(propertyPrevProjects)) {
            prevProjects.load(input);
        }
        return prevProjects;
    }
    
    /**
     * Saves a new scanned project in a properties file.
     * 
     * @param path
     * @throws IOException 
     */
    public void setNewPrevProject(String path) throws IOException {
        String projectName = getProjectName(path);
        Properties prevProjects = new Properties();
        try (FileInputStream input = new FileInputStream(propertyPrevProjects)) {
            prevProjects.load(input);
        }
        try (FileOutputStream output = new FileOutputStream(propertyPrevProjects)) {
            if (prevProjects.getProperty(projectName) == null) {
                prevProjects.setProperty(projectName, path);
            }
            prevProjects.store(output, null);
        }
    }
    
    /**
     * Checks if the file 'prev_projects.properties' already exists.
     * If not, create a new one.
     * 
     * @throws IOException 
     */
    private void checkForPrevProjectFile() throws IOException {
        File propertyFile = new File(propertyPrevProjects);
        if (!propertyFile.exists()) {
            propertyFile.createNewFile();
        }
    }
    
    /**
     * Gets the project name of the set project path.
     * 
     * @param path
     * @return project name
     */
    private String getProjectName(String path) {
        String newPath = path.replace("\\", ";");
        String[] values = newPath.split(";");
        int maxIndex = values.length - 1;
        return values[maxIndex];
    }
}
