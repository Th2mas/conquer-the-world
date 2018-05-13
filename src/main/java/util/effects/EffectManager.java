package util.effects;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: Implement this manager

/**
 * A manager, which handles different effects used in the game
 * Effects should run periodically until the effect should be stopped
 */
public class EffectManager {

    private static EffectManager effectManager;

    private ExecutorService executorService;

    private EffectManager(){}

    public static synchronized EffectManager getEffectManager(){
        if(effectManager == null){
            effectManager = new EffectManager();
        }
        return effectManager;
    }

    public void addEffect(Effect effect){
        // TODO: Implement me correctly
        if(executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        executorService.execute(effect);
    }

    public void stopEffect(Effect effect){
        // TODO: Implement me
    }
}
