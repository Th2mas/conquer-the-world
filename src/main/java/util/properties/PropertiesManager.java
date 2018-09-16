package util.properties;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A manager for a given {@code .properties} file
 * TODO: Make a static manager with a map as reference!
 */
public class PropertiesManager {

    /**
     * The resource bundle for managing the file
     */
    private ResourceBundle bundle;

    /**
     * The name of the file used for the ResourceBundle
     */
    private final String bundleName;

    /**
     * Creates a new PropertiesManager
     * @param name the name of the requested properties file
     */
    public PropertiesManager(String name){

        if(name.endsWith(".properties"))
            name = name.substring(0, name.length()-".properties".length());

        bundleName = (name.contains("/")?name.substring(name.indexOf("/")+1):name);
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
     * Returns a list of strings, which start with startsWith
     * @param startsWith the string, which will be used for filtering
     * @return a list with all filtered strings
     */
    public List<String> getAllStrings(String startsWith) {
        return keySet().stream()
                .filter(s -> s.startsWith(startsWith))
                .map(this::getString)
                .collect(Collectors.toList());
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
     * Change the ResourceBundles locale
     * @param locale the locale for language support
     */
    public void changeLocale(Locale locale){
        Locale loc = null;
        for(Locale l : getSupportedLocales())
            if(l.getLanguage().equals(locale.getLanguage()))
                loc = l;
        if(loc == null) loc = new Locale("en");
        bundle = ResourceBundle.getBundle(bundleName, loc);
    }

    /**
     * Returns a list of supported locales
     * @return a list of supported locales; the list is empty, if there are no files
     */
    public List<Locale> getSupportedLocales(){
        List<Locale> list = new ArrayList<>();

        File f = new File(PropertiesManager.class.getResource("/properties").getFile());
        File[] files = f.listFiles();

        // Checks if there are any files in the Resource bundle
        if(files == null) {
            System.err.println("Error: \"properties\" does not exist");
            return list;
        }

        // Iterate over all files in the "properties" directory
        for (File file : files){
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("."));
            if(name.contains(bundleName) && name.contains("_"))
                list.add(new Locale(name.substring(name.indexOf("_")+1)));
        }

        return list;
    }

    /**
     * Returns all keys of the {@code .properties} file
     * @return all keys
     */
    public Set<String> keySet(){
        return bundle.keySet();
    }

    /**
     * Returns the bundle
     * @return bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }
}
