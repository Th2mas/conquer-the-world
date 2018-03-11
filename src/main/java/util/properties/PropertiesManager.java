package util.properties;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * A manager for a given {@code .properties} file
 */
public class PropertiesManager {

    /**
     * The resource bundle for managing the file
     */
    private final ResourceBundle bundle;

    /**
     * Creates a new PropertiesManager
     * @param name the name of the requested properties file
     */
    public PropertiesManager(String name){
        if(name.endsWith(".properties"))
            this.bundle = ResourceBundle.getBundle(name.substring(0, name.length()-".properties".length()));
        else
            this.bundle = ResourceBundle.getBundle(name);
    }

    /**
     * Returns the string to the given key
     * @param key the key to the requested value
     * @return the requested string
     */
    public String getString(String key){
        return bundle.getString(key);
    }

    /**
     * Returns the int to the given key
     * @param key the key to the requested value
     * @return the requested int
     */
    public int getInt(String key){
        return Integer.parseInt(bundle.getString(key));
    }

    /**
     * Returns the boolean to the given key
     * @param key the key to the requested value
     * @return the requested boolean
     */
    public boolean getBoolean(String key){
        return Boolean.parseBoolean(bundle.getString(key));
    }

    /**
     * Returns all keys of the {@code .properties} file
     * @return all keys
     */
    public Set<String> keySet(){
        return bundle.keySet();
    }
}
