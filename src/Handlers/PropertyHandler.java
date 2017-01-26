package Handlers;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author David
 */
public class PropertyHandler {

    private final String propertyFileName = "sq_helper.properties";
    
    /**
     * Checks if the property file 'sq_helper.properties' already exists.
     * If the file doesn't exist, a new file with the name 'sql_helper.properties'
     * is created.
     * @return true if it exists, else false
     * @throws java.io.IOException
     */
    public boolean checkForPropertyFile() throws IOException {
        File propertyFile = new File("sq_helper.properties");
        if (propertyFile.exists()) {
            return true;
        } else {
            propertyFile.createNewFile();
            return false;
        }
    }
}
