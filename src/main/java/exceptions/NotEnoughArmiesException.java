package exceptions;

import dto.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotEnoughArmiesException extends Exception {

    private static final Logger LOG = LoggerFactory.getLogger(NotEnoughArmiesException.class);

    public NotEnoughArmiesException(Player player){
        LOG.info("Player " + player.getName() + " does not have enough armies to attack a country");
    }
}
