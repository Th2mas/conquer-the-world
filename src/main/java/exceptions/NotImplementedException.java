package exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotImplementedException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(NotImplementedException.class);

    public NotImplementedException(){

    }
}
