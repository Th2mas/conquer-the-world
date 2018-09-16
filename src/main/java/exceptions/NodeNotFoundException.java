package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This exception will be thrown, if a node was not found
 */
public class NodeNotFoundException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeNotFoundException.class);

    /**
     * Prints the given node as an exception
     * @param nodeName Node which was not found
     */
    public NodeNotFoundException(String nodeName){
        LOGGER.info(String.format("Node with id '%s' not found", nodeName));
    }
}
