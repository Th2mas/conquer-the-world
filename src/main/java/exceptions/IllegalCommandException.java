package exceptions;

/**
 * Indicates, that an invalid / illegal command happened
 */
public class IllegalCommandException extends Exception {

    /**
     * Creates a new illegal command exception
     * @param s error message
     */
    public IllegalCommandException(String s){
        super(s);
    }
}
