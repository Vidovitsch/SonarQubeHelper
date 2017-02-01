package enums;

/**
 *
 * @author David
 */
public enum opsystem {
    WIN64, WIN32, LIN64, LIN32, MAC64;
    
    /**
     * Converts a enum value to a corresponding String value.
     * 
     * @param system
     * @return String
     */
    public String getSystemValue(opsystem system) {
        switch(system) {
            case WIN64:
                return "windows-x86-64";
            case WIN32:
                return "windows-x86-32";
            case LIN64:
                return "linux-x86-64";
            case LIN32:
                return "linux-x86-32";
            default:
                return "macosx-universal-64";
        }
    }
    
    /**
     * Converts a String value to a corresponding enum value.
     * 
     * @param systemValue
     * @return opsystem
     */
    public static opsystem getEnumValue(String systemValue) {
        switch(systemValue) {
            case "windows-x86-64":
                return opsystem.WIN64;
            case "windows-x86-32":
                return opsystem.WIN32;
            case "linux-x86-64":
                return opsystem.LIN64;
            case "linux-x86-32":
                return opsystem.LIN32;
            default:
                return opsystem.MAC64;
        }
    }
    
    /**
     * Converts a enum value to a corresponding file path.
     * 
     * @param system
     * @return String
     */
    public String getFilePath(opsystem system) {
        switch(system) {
            case WIN64:
                return "\\windows-x86-64\\StartSonar.bat";
            case WIN32:
                return "\\windows-x86-32\\StartSonar.bat";
            case LIN64:
                return "\\linux-x86-64\\sonar.sh";
            case LIN32:
                return "\\linux-x86-32\\sonar.sh";
            default:
                return "\\macosx-universal-64\\sonar.sh";
        }
    }
}
