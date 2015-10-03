/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.prefs.Preferences;

/**
 *
 * @author Jaishreeganesh
 */
public class CloudUtil {
        
    public static void setPreferences (String name, String value) {
        Preferences prefs = Preferences.userNodeForPackage(CloudUtil.class);
        prefs.put(name, value);
    }
}
