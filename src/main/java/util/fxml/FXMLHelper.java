package util.fxml;

import javafx.fxml.FXMLLoader;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A helper class for loading fxml files
 */
public class FXMLHelper {

    /**
     * Loads the fxml file and returns the loader to this file
     * @param file the path of the file in the resources directory
     * @return the loader of the file
     */
    private static FXMLLoader getFXMLLoader(String file) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLHelper.class.getResource(file));
        return loader;
    }

    /**
     * Loads a fxml file
     * @param file the path of the file in the resources directory
     * @return the root object of the file
     * @throws IOException will be thrown, if an exception occured
     */
    public static Object loadFXMLFile(String file) throws IOException {
        return getFXMLLoader(file).load();
    }

    /**
     * Load the controller of the fxml file (default properties: lang)
     * @param file the path of the file in the resources directory
     * @return the controller of the fxml file
     * @throws IOException will be thrown, if an exception occured
     */
    public static <T> T loadFXMLController(String file) throws IOException {
        return loadFXMLControllerWithResources(file,(new PropertiesManager("properties/lang")).getBundle());
    }

    /**
     * Load the controller of the fxml file with a resource bundle
     * @param file the path of the file in the resources directory
     * @param bundle the bundle, which can be used
     * @return the controller of the fxml file
     * @throws IOException will be thrown, if an exception occured
     */
    public static <T> T loadFXMLControllerWithResources(String file, ResourceBundle bundle) throws IOException {
        FXMLLoader loader = getFXMLLoader(file);

        // Set the resource bundle
        loader.setResources(bundle);

        // Loader the actual fxml file
        loader.load();

        // Return the controller
        return loader.getController();
    }
}
