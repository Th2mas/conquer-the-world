package util.fxml;

import javafx.fxml.FXMLLoader;
import util.properties.PropertiesManager;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * A helper class for loading fxml files
 */
public class FXMLHelper {

    // Private
    private FXMLHelper() {}

    /**
     * Loads the fxml file and returns the loader to this file
     * @param file the path of the file in the resources directory
     * @return the loader of the file
     */
    private static FXMLLoader getFXMLLoader(String file) {
        return getFXMLLoaderWithResources(file, PropertiesManager.getBundle("lang"));
    }

    /**
     * Loads the fxml file with resources and returns the loader to this file
     * @param file the path of the file in the resources directory
     * @param bundle the bundle, which can be used
     * @return the loader of the file
     */
    private static FXMLLoader getFXMLLoaderWithResources(String file, ResourceBundle bundle) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLHelper.class.getResource(file));
        loader.setResources(bundle);
        return loader;
    }

    /**
     * Loads a fxml file
     * @param file the path of the file in the resources directory
     * @return the root object of the file
     * @throws IOException will be thrown, if an exception occurred
     */
    public static Object loadFXMLFile(String file) throws IOException {
        return getFXMLLoader(file).load();
    }

    /**
     * Loads a fxml file with resources
     * @param file the path of the file in the resources directory
     * @param bundle the bundle, which can be used
     * @return the root object of the file
     * @throws IOException will be thrown, if an exception occurred
     */
    public static Object loadFXMLFileWithResources(String file, ResourceBundle bundle) throws IOException {
        return getFXMLLoaderWithResources(file, bundle).load();
    }

    /**
     * Load the controller of the fxml file (default properties: lang)
     * @param file the path of the file in the resources directory
     * @return the controller of the fxml file
     * @throws IOException will be thrown, if an exception occurred
     */
    public static <T> T loadFXMLController(String file) throws IOException {
        return loadFXMLControllerWithResources(file, PropertiesManager.getBundle("lang"));
    }

    /**
     * Load the controller of the fxml file with a resource bundle
     * @param file the path of the file in the resources directory
     * @param bundle the bundle, which can be used
     * @return the controller of the fxml file
     * @throws IOException will be thrown, if an exception occurred
     */
    public static <T> T loadFXMLControllerWithResources(String file, ResourceBundle bundle) throws IOException {
        FXMLLoader loader = getFXMLLoaderWithResources(file, bundle);

        // Loader the actual fxml file
        loader.load();

        // Return the controller
        return loader.getController();
    }
}
