package me.rufia.fightorflight.compat;

//import necro.livelier.pokemon.common.config.CategoryCache;

import java.util.Set;

@Deprecated
public class LivelierPokemonCompat  {
    private static boolean loaded = false;

    public static String getModID() {
        return "livelierpokemon";
    }

    public static void load(boolean value) {
        loaded = value;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
