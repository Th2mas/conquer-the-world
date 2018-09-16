package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotImplementedException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotImplementedException.class);

    public NotImplementedException(){
        LOGGER.info("Not implemented yet!");
    }
}
