package enums;

/**
 *
 * @author David
 */
public enum OpSystem {
    WIN64, WIN32, LIN64, LIN32, MAC64;
    
    public String getSystemValue(OpSystem system) {
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
    
    public static OpSystem getEnumValue(String systemValue) {
        switch(systemValue) {
            case "windows-x86-64":
                return OpSystem.WIN64;
            case "windows-x86-32":
                return OpSystem.WIN32;
            case "linux-x86-64":
                return OpSystem.LIN64;
            case "linux-x86-32":
                return OpSystem.LIN32;
            default:
                return OpSystem.MAC64;
        }
    }
    
    public String getFilePath(OpSystem system) {
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
