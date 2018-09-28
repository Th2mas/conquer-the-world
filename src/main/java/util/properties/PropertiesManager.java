package util.properties;

import exceptions.LanguageNotSupportedException;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * The list of supported locales
     */
    // TODO: Change that, so it can load all locales dynamically (read it from the directory)
    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("de"),
            Locale.forLanguageTag("es"),
            Locale.forLanguageTag("ru")
    );

    /**
     * The list of supported bundles, which are not language bundles
     */
    // TODO: Change that, so it can load all bundles dynamically (read it from the directory)
    private static final List<String> SUPPORTED_BUNDLES = Arrays.asList(
            "custom",
            "settings",
            "window"
    );

    /**
     * The list of supported bundles with different languages
     */
    private static final List<String> SUPPORTED_LANGUAGE_BUNDLES = Collections.singletonList("lang");

    /**
     * The current locale
     */
    private static Locale locale = Locale.getDefault();

    /**
     * The current language
     */
    private static String currentLanguage = "";

    /**
     * Indicates, if the PropertiesManager was already started
     */
    private static boolean isInitialized = false;

    /**
     * The list of subscribers, which will be notified on change
     */
    private static final List<StringProperty> subscribers = new ArrayList<>();

    /**
     * The map for all language bundles
     */
    private static final Map<String, ResourceBundle> BUNDLES = new HashMap<>();

    /**
     * Initializes the PropertiesManager
     */
    public static void initialize() {
        if(!isInitialized) {
            SUPPORTED_LOCALES.forEach(locale -> {
                SUPPORTED_LANGUAGE_BUNDLES.forEach(lang -> {
                    BUNDLES.put(locale.getLanguage(), ResourceBundle.getBundle("properties." + lang, locale));
                });
            });

            SUPPORTED_BUNDLES.forEach(bundle -> {
                BUNDLES.put(bundle, ResourceBundle.getBundle("properties." + bundle));
            });

            isInitialized = true;
        }
    }

    // Private
    private PropertiesManager() {}

    /**
     * Get the bundle with the given name
     * @param name the bundle's name
     * @return the bundle
     */
    public static ResourceBundle getBundle(String name) {
        if(name.equalsIgnoreCase("lang")) return getBundle(locale.getLanguage());
        return BUNDLES.get(name);
    }

    /**
     * Changes the locale, if supported
     * @param locale the locale, which will be used
     */
    private static void changeLocale(Locale locale) {
        if (!SUPPORTED_LOCALES.contains(locale)) {
            throw new LanguageNotSupportedException(locale.getLanguage());
        }

        PropertiesManager.locale = locale;

        // Reset the language string
        currentLanguage = "";
    }

    /**
     * Returns the string to the given key
     * @param key the key to the requested value
     * @param bundle the bundle, which is used for getting the value
     * @return the requested string
     */
    private static String getString(String key, ResourceBundle bundle){
        return bundle.getString(key);
    }

    /**
     * Returns the string to the given key
     * @param key the key to the requested value
     * @param bundleName the name of the bundle
     * @return the requested string
     */
    public static String getString(String key, String bundleName) { return getString(key, getBundle(bundleName)); }

    /**
     * Returns a list of strings, which start with startsWith
     * @param startsWith the string, which will be used for filtering
     * @param bundle the bundle, which is used for getting the value
     * @return a list with all filtered strings
     */
    private static List<String> getAllStrings(String startsWith, ResourceBundle bundle) {
        return bundle.keySet().stream()
                .filter(s -> s.startsWith(startsWith))
                .map(s -> PropertiesManager.getString(s, bundle))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of strings, which start with startsWith
     * @param startsWith the string, which will be used for filtering
     * @param bundleName the name of the bundle
     * @return a list with all filtered strings
     */
    public static List<String> getAllStrings(String startsWith, String bundleName) {
        return getAllStrings(startsWith, getBundle(bundleName));
    }

    /**
     * Returns the int to the given key
     * @param key the key to the requested value
     * @param bundle the bundle, which is used for getting the value
     * @return the requested int
     */
    private static int getInt(String key, ResourceBundle bundle){
        return Integer.parseInt(bundle.getString(key));
    }

    /**
     * Returns the int to the given key
     * @param key the key to the requested value
     * @param bundleName the name of the bundle
     * @return the requested int
     */
    public static int getInt(String key, String bundleName) { return getInt(key, getBundle(bundleName)); }

    /**
     * Returns the boolean to the given key
     * @param key the key to the requested value
     * @param bundle the bundle, which is used for getting the value
     * @return the requested boolean
     */
    private static boolean getBoolean(String key, ResourceBundle bundle){
        return Boolean.parseBoolean(bundle.getString(key));
    }

    /**
     * Returns the boolean to the given key
     * @param key the key to the requested value
     * @param bundleName the name of the bundle
     * @return the requested boolean
     */
    public static boolean getBoolean(String key, String bundleName) {
        return getBoolean(key, getBundle(bundleName));
    }

    /**
     * Change the language
     * @param language the language to be switched to
     */
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

        // Notify all text objects, that the language has changed!
        // Use an empty string, so that you can check visually, if you have added a change listener to the property
        subscribers.forEach(s -> s.setValue(""));
    }

    /**
     * Get the current language
     * @return the current language
     */
    public static String getCurrentLanguage() {
        if(currentLanguage.isEmpty()) {
            // Get the settings manager
            ResourceBundle bundle = getBundle("settings");

            // Get the locale's key in the properties file (Format: 'Language.')
            String key = getKey(locale.getLanguage(), bundle);

            // Remove the 'Locale.' and extract only the language
            key = key.substring(key.indexOf('.'));

            // Get the language
            currentLanguage = getString("Language" + key, bundle);
        }

        return currentLanguage;
    }

    /**
     * Returns the key to a given value (value must be unique)
     * @param value the value of the key
     * @param bundle the bundle, in which the key needs to be searched for
     * @return the key
     */
    private static String getKey(String value, ResourceBundle bundle) {
        String key = "";

        for(String k : bundle.keySet())
            if(getString(k, bundle).equals(value))
                key = k;

        return key;
    }

    /**
     * Add a subscriber, which will be notified, if something has changed
     * @param property the string property to be changed
     */
    public static void addSubscriber(StringProperty property) {
        subscribers.add(property);
    }
}
