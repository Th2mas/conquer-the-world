package exceptions;

import dto.Country;
import dto.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryNotAvailableException extends Exception {

    private static final Logger LOG = LoggerFactory.getLogger(CountryNotAvailableException.class);

    public CountryNotAvailableException(Player player, Country country){
        LOG.info("Country " + country.getName() + " not available. Belongs to " + player.getName());
    }
}
