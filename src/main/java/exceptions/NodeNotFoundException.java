package exceptions;

/**
 * This exception will be thrown, if a node was not found
 */
public class NodeNotFoundException extends Exception {

    /**
     * Prints the given node as an exception
     * @param nodeName Node which was not found
     */
    public NodeNotFoundException(String nodeName){
        super(String.format("Node with id '%s' not found", nodeName));
    }
}
