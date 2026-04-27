package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TargetingWhitelist {
    private static final Set<String> ALL_POKEMON;
    private static final Set<String> WILD_POKEMON;
    private static final Set<String> PLAYER_OWNED_POKEMON;

    static {
        ALL_POKEMON = new HashSet<>();
        WILD_POKEMON = new HashSet<>();
        PLAYER_OWNED_POKEMON = new HashSet<>();
    }

    public static void init() {
        ALL_POKEMON.addAll(Arrays.stream(CobblemonFightOrFlight.commonConfig().all_pokemon_targeting_whitelist).toList());
        PLAYER_OWNED_POKEMON.addAll(Arrays.stream(CobblemonFightOrFlight.commonConfig().player_owned_pokemon_targeting_whitelist).toList());
        PLAYER_OWNED_POKEMON.addAll(ALL_POKEMON);
        WILD_POKEMON.addAll(Arrays.stream(CobblemonFightOrFlight.commonConfig().wild_pokemon_targeting_whitelist).toList());
        WILD_POKEMON.addAll(ALL_POKEMON);
        //debug();
    }

    public static Set<String> getWhitelist(boolean hasOwner) {
        return hasOwner ? PLAYER_OWNED_POKEMON : WILD_POKEMON;
    }

    public static Set<String> getWhitelist(PokemonEntity pokemonEntity) {
        return getWhitelist(pokemonEntity.getPokemon());
    }

    public static Set<String> getWhitelist(Pokemon pokemon) {
        return getWhitelist(pokemon.isPlayerOwned());
    }

    private static void debug(){
        for(var s:ALL_POKEMON){
            CobblemonFightOrFlight.LOGGER.info("ALL_POKEMON:{}",s);
        }
        for(var s:WILD_POKEMON){
            CobblemonFightOrFlight.LOGGER.info("WILD_POKEMON:{}",s);
        }
        for(var s:PLAYER_OWNED_POKEMON){
            CobblemonFightOrFlight.LOGGER.info("PLAYER_OWNED_POKEMON:{}",s);
        }
    }
}
