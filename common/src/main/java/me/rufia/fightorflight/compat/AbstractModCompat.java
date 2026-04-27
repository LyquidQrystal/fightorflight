package me.rufia.fightorflight.compat;

import me.rufia.fightorflight.CobblemonFightOrFlight;

import java.util.function.Predicate;

public abstract class AbstractModCompat {

    private boolean loaded = false;

    public abstract String getModID();

    public void setLoaded(boolean value) {
        loaded = value;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void tryLoad(Predicate<String> predicate) {
        if (predicate.test(getModID())) {
            setLoaded(true);
            CobblemonFightOrFlight.LOGGER.info("[FOF] {} is loaded", getModID());
        }
    }
}
