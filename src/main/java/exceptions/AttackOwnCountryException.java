package exceptions;

import dto.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackOwnCountryException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttackOwnCountryException.class);

    public AttackOwnCountryException(Player player){
        LOGGER.info("Player " + player.getName() + " tries to attack himself");
    }
}
