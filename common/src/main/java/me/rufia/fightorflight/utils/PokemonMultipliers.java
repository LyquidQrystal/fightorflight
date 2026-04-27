package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;

public class PokemonMultipliers {
    private final boolean hasOwner;

    public PokemonMultipliers(PokemonEntity pokemonEntity) {
        hasOwner = pokemonEntity.getOwner() != null;
    }

    public float getMinimumAttackDamage() {
        return CobblemonFightOrFlight.commonConfig().minimum_attack_damage * getMinimumAttackDamageMultiplier();
    }

    public float getMinimumAttackDamageMultiplier() {
        return hasOwner ? CobblemonFightOrFlight.commonConfig().minimum_attack_damage_player : 1.0f;
    }

    public float getMaximumAttackDamage() {
        return CobblemonFightOrFlight.commonConfig().maximum_attack_damage * getMaximumAttackDamageMultiplier();
    }

    public float getMaximumAttackDamageMultiplier() {
        return hasOwner ? CobblemonFightOrFlight.commonConfig().maximum_attack_damage_player : 1.0f;
    }

    public float getMinimumRangeAttackDamage() {
        return CobblemonFightOrFlight.commonConfig().minimum_ranged_attack_damage * getMinimumRangeAttackDamageMultiplier();
    }

    public float getMinimumRangeAttackDamageMultiplier() {
        return hasOwner ? CobblemonFightOrFlight.commonConfig().minimum_ranged_attack_damage_player : 1.0f;
    }

    public float getMaximumRangeAttackDamage() {
        return CobblemonFightOrFlight.commonConfig().maximum_ranged_attack_damage * getMaximumRangeAttackDamageMultiplier();
    }

    public float getMaximumRangeAttackDamageMultiplier() {
        return hasOwner ? CobblemonFightOrFlight.commonConfig().maximum_ranged_attack_damage_player : 1.0f;
    }

    public float getMaximumDamageReduction() {
        return CobblemonFightOrFlight.commonConfig().max_damage_reduction_multiplier * (hasOwner ? CobblemonFightOrFlight.commonConfig().max_damage_reduction_multiplier_player : 1.0f);
    }

    public float getPlayerOwnedDamageMultiplier(boolean isRangeAttack, boolean isMeleeAttack) {
        if (isRangeAttack) {
            return hasOwner ? CobblemonFightOrFlight.commonConfig().ranged_attack_damage_player : 1.0f;
        }
        if (isMeleeAttack) {
            return hasOwner ? CobblemonFightOrFlight.commonConfig().attack_damage_player : 1.0f;
        }
        return 1.0f;
    }
}
