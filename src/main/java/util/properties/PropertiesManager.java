package util.properties;

import exceptions.LanguageNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A manager for all properties
 */
public class PropertiesManager {

    /**
     * The {@link PropertiesManager} logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesManager.class);

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("de"),
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("ru")
    );

    private static final List<String> SUPPORTED_BUNDLES = Arrays.asList(
            "custom",
            "settings",
            "window"
    );

    /**
     * The name of the file used for the ResourceBundle
     */
    private static final String LANG = "lang";

    private static Locale locale = Locale.getDefault();

    private static boolean isInitialized = false;

    /**
     * The map for all language bundles
     */
    private static final Map<String, ResourceBundle> BUNDLES = new HashMap<>();

    public static void initialize() {
        if(!isInitialized) {
            SUPPORTED_LOCALES.forEach(locale -> {
                BUNDLES.put(locale.getLanguage(), ResourceBundle.getBundle("properties." + LANG, locale));
            });

            SUPPORTED_BUNDLES.forEach(bundle -> {
                BUNDLES.put(bundle, ResourceBundle.getBundle("properties." + bundle));
            });

            isInitialized = true;
        }
    }

    private PropertiesManager() {}

    public static ResourceBundle getBundle(String name) {
        if(name.equalsIgnoreCase("lang")) return getBundle(locale.getLanguage());
        return BUNDLES.get(name);
    }

    private static void changeLocale(Locale locale) {
        if (!SUPPORTED_LOCALES.contains(locale)) {
            throw new LanguageNotSupportedException(locale.getLanguage());
        }
        PropertiesManager.locale = locale;
    }

    /**
     * Returns the string to the given key
     * @param key the key to the requested value
     * @return the requested string
     */
    private static String getString(String key, ResourceBundle bundle){
        return bundle.getString(key);
    }

    public static String getString(String key, String bundleName) { return getString(key, getBundle(bundleName)); }

    /**
     * Returns a list of strings, which start with startsWith
     * @param startsWith the string, which will be used for filtering
     * @return a list with all filtered strings
     */
    private static List<String> getAllStrings(String startsWith, ResourceBundle bundle) {
        return bundle.keySet().stream()
                .filter(s -> s.startsWith(startsWith))
                .map(s -> PropertiesManager.getString(s, bundle))
                .collect(Collectors.toList());
    }

    public static List<String> getAllStrings(String startsWith, String bundleName) {
        return getAllStrings(startsWith, getBundle(bundleName));
    }

    /**
     * Returns the int to the given key
     * @param key the key to the requested value
     * @return the requested int
     */
    private static int getInt(String key, ResourceBundle bundle){
        return Integer.parseInt(bundle.getString(key));
    }

    public static int getInt(String key, String bundleName) { return getInt(key, getBundle(bundleName)); }

    /**
     * Returns the boolean to the given key
     * @param key the key to the requested value
     * @return the requested boolean
     */
    private static boolean getBoolean(String key, ResourceBundle bundle){
        return Boolean.parseBoolean(bundle.getString(key));
    }

    public static boolean getBoolean(String key, String bundleName) {
        return getBoolean(key, getBundle(bundleName));
    }

    public static void changeLanguage(String language) {
        // Get the settings manager
        ResourceBundle bundle = getBundle("settings");

        // Check if the language is a supported language
        if(!getAllStrings("Language", bundle).contains(language)) throw new LanguageNotSupportedException(language);

        // Get the language's key in the properties file (Format: 'Language.')
        String key = getKey(language, bundle);

        // Remove the 'Language.' and extract only the language
        if(!key.contains(".")) return;
        key = key.substring(key.indexOf('.'));

        // Get the locale
        Locale locale = Locale.forLanguageTag(getString("Locale" + key, bundle));

        // Change the locale
        changeLocale(locale);

        // TODO: Notify all text objects, that the language has changed!
    }

    private static String getKey(String value, ResourceBundle bundle) {
        String key = "";

        for(String k : bundle.keySet())
            if(getString(k, bundle).equals(value))
                key = k;

        return key;
    }
}
