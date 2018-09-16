package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageNotSupportedException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageNotSupportedException.class);

    public LanguageNotSupportedException(String s) {
        LOGGER.error("Language " + s + " is not supported!");
    }
}
