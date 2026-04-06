package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FOFAggressionCalculator {
    private static final List<Double> sideModExtraAggressionList = new ArrayList<>();

    public static double getAggressiveValue() {
        return 100 + CobblemonFightOrFlight.commonConfig().aggressive_threshold;
    }

    public static double getNeutralValue() {
        return (CobblemonFightOrFlight.commonConfig().neutral_threshold + CobblemonFightOrFlight.commonConfig().aggressive_threshold) * 0.5;
    }

    public static double getPeacefulValue() {
        return -100 - Math.abs(CobblemonFightOrFlight.commonConfig().neutral_threshold);
    }

    protected static double getAtkDefDifCoefficient(Pokemon pokemon) {
        return (double) ((pokemon.getAttack() + pokemon.getSpecialAttack()) - (pokemon.getDefence() + pokemon.getSpecialDefence())) / pokemon.getLevel() * CobblemonFightOrFlight.commonConfig().aggression_atk_def_dif_base_value;
    }

    protected static double getDarknessAggressionCoefficient(PokemonEntity pokemonEntity) {
        boolean checkGhostType = CobblemonFightOrFlight.commonConfig().ghost_light_level_aggro && PokemonUtils.hasType(pokemonEntity, ElementalTypes.GHOST);
        boolean checkDarkType = CobblemonFightOrFlight.commonConfig().dark_light_level_aggro && PokemonUtils.hasType(pokemonEntity, ElementalTypes.DARK);
        if (checkGhostType || checkDarkType) {
            int skyDarken = pokemonEntity.level().getSkyDarken();
            int lightLevel = pokemonEntity.level().getRawBrightness(pokemonEntity.blockPosition(), skyDarken);
            if (lightLevel <= 7) {
                return CobblemonFightOrFlight.commonConfig().aggression_light_level_base_value;
            } else if (lightLevel >= 12) {
                return -CobblemonFightOrFlight.commonConfig().aggression_light_level_base_value;
            }
        }
        return 0;
    }

    protected static double getIntimidateCoefficient(PokemonEntity pokemonEntity) {
        var pokemons = pokemonEntity.level().getEntitiesOfClass(PokemonEntity.class, AABB.ofSize(pokemonEntity.position(), 18, 18, 18), (pokemonEntity1) -> pokemonEntity1.getOwner() != null && Arrays.stream(CobblemonFightOrFlight.commonConfig().aggro_reducing_abilities).toList().contains(pokemonEntity1.getPokemon().getAbility().getName()));

        if (!pokemons.isEmpty()) {
            return CobblemonFightOrFlight.commonConfig().aggression_intimidation_base_value;
        }
        return 0;
    }

    protected static double getLevelAggressionCoefficient(PokemonEntity pokemonEntity) {
        return CobblemonFightOrFlight.commonConfig().aggression_level_base_value * CobblemonFightOrFlight.commonConfig().aggression_level_multiplier * pokemonEntity.getPokemon().getLevel() / 100;
    }

    protected static double getNatureAggressionCoefficient(Pokemon pokemon) {
        String natureName = PokemonUtils.getNatureName(pokemon);
        double multiplier = 0;
        if (Arrays.stream(CobblemonFightOrFlight.commonConfig().more_aggressive_nature).toList().contains(natureName)) {
            multiplier = CobblemonFightOrFlight.commonConfig().more_aggressive_nature_multiplier;
        } else if (Arrays.stream(CobblemonFightOrFlight.commonConfig().aggressive_nature).toList().contains(natureName)) {
            multiplier = CobblemonFightOrFlight.commonConfig().aggressive_nature_multiplier;
        } else if (Arrays.stream(CobblemonFightOrFlight.commonConfig().peaceful_nature).toList().contains(natureName)) {
            multiplier = CobblemonFightOrFlight.commonConfig().peaceful_nature_multiplier;
        } else if (Arrays.stream(CobblemonFightOrFlight.commonConfig().more_peaceful_nature).toList().contains(natureName)) {
            multiplier = CobblemonFightOrFlight.commonConfig().more_peaceful_nature_multiplier;
        }
        return multiplier * CobblemonFightOrFlight.commonConfig().aggression_nature_base_value;
    }

    //Extra aggression offered by mods that FOF has an integrated support.
    protected static double getSupportedModExtraAggression(PokemonEntity pokemonEntity) {
        double result = 0;
        if (CobblemonFightOrFlight.sizeVariationCompat.isLoaded()) {
            result += CobblemonFightOrFlight.sizeVariationCompat.getExtraAggression(pokemonEntity);
        }
        //CobblemonFightOrFlight.LOGGER.info("{}:{}", pokemonEntity.getPokemon().getDisplayName(false), result);
        return result;
    }

    //Use mixins, inject at the head add your value to sideModExtraAggressionList, and it should work? To be honest, I'm not sure if anyone will try this.
    protected static double getSideModExtraAggression(PokemonEntity pokemonEntity) {
        double result = 0;
        for (double d : sideModExtraAggressionList) {
            result += d;
        }
        return result;
    }

    public static double calc(PokemonEntity pokemonEntity) {
        if (pokemonEntity != null) {
            return getAtkDefDifCoefficient(pokemonEntity.getPokemon())
                    + getDarknessAggressionCoefficient(pokemonEntity)
                    + getIntimidateCoefficient(pokemonEntity)
                    + getLevelAggressionCoefficient(pokemonEntity)
                    + getNatureAggressionCoefficient(pokemonEntity.getPokemon())
                    + getSupportedModExtraAggression(pokemonEntity)
                    + getSideModExtraAggression(pokemonEntity);
        }
        return 0;
    }

}
