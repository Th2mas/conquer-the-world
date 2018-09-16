package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Indicates, that an invalid / illegal command happened
 */
public class IllegalCommandException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(IllegalCommandException.class);

    /**
     * Creates a new illegal command exception
     * @param s dialog message
     */
    public IllegalCommandException(String s){
        LOGGER.info(s);
    }
}
