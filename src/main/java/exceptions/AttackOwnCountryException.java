package exceptions;

import dto.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackOwnCountryException extends Exception {

    private static final Logger LOG = LoggerFactory.getLogger(AttackOwnCountryException.class);

    public AttackOwnCountryException(Player player){
        LOG.info("Player " + player.getName() + " tries to attack himself");
    }
}
