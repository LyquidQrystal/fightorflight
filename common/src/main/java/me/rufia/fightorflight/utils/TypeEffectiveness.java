package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;

public class TypeEffectiveness {
    public static float getTypeEffectiveness(PokemonEntity offense, PokemonEntity defense) {
        return getTypeEffectiveness(offense, defense, true);
    }

    public static float getTypeEffectiveness(PokemonEntity offense, PokemonEntity defense, boolean shouldCheckAbility) {
        if (!CobblemonFightOrFlight.commonConfig().type_effectiveness_between_pokemon) {
            return 1f;
        }
        Move move = PokemonUtils.getMove(offense);

        float result;
        if (move != null) {
            result = getTypeEffectiveness(move, defense.getPokemon().getPrimaryType());
            if (defense.getPokemon().getSecondaryType() != null) {
                result *= getTypeEffectiveness(move, defense.getPokemon().getSecondaryType());
            }
        } else {
            ElementalType offenseType = offense.getPokemon().getPrimaryType();
            result = getTypeEffectivenessSimple(offenseType, defense);
        }
        return abilityCheck(offense, defense, result, shouldCheckAbility);
    }

    public static float getTypeEffectiveness(Move offenseMove, ElementalType defenseType) {
        if (offenseMove.getName().equals("freezedry")) {
            if (defenseType.getName().equals("water")) {
                return 2f;
            }
        }
        float result = getTypeEffectiveness(offenseMove.getType(), defenseType);
        if (offenseMove.getName().equals("flyingpress")) {
            result *= getTypeEffectiveness("flying", defenseType.getName());
        }
        return result;
    }

    public static float getTypeEffectiveness(ElementalType offenseType, ElementalType defenseType) {
        return getTypeEffectiveness(offenseType.getName(), defenseType.getName());
    }

    public static float getTypeEffectivenessSimple(ElementalType offenseType, PokemonEntity defendingPokemon) {
        float result = getTypeEffectiveness(offenseType, defendingPokemon.getPokemon().getPrimaryType());
        if (defendingPokemon.getPokemon().getSecondaryType() != null) {
            result *= getTypeEffectiveness(offenseType, defendingPokemon.getPokemon().getSecondaryType());
        }
        return result;
    }

    public static float getTypeEffectivenessSimple(String typeName, PokemonEntity defendingPokemon) {
        float result = getTypeEffectiveness(typeName, defendingPokemon.getPokemon().getPrimaryType().getName());
        if (defendingPokemon.getPokemon().getSecondaryType() != null) {
            result *= getTypeEffectiveness(typeName, defendingPokemon.getPokemon().getSecondaryType().getName());
        }
        return result;
    }

    public static float getTypeEffectiveness(String offenseTypeName, String defenseTypeName) {
        return switch (offenseTypeName) {
            case "normal" -> normalOffense(defenseTypeName);
            case "fighting" -> fightingOffense(defenseTypeName);
            case "flying" -> flyingOffense(defenseTypeName);
            case "poison" -> poisonOffense(defenseTypeName);
            case "ground" -> groundOffense(defenseTypeName);
            case "rock" -> rockOffense(defenseTypeName);
            case "bug" -> bugOffense(defenseTypeName);
            case "ghost" -> ghostOffense(defenseTypeName);
            case "steel" -> steelOffense(defenseTypeName);
            case "fire" -> fireOffense(defenseTypeName);
            case "water" -> waterOffense(defenseTypeName);
            case "grass" -> grassOffense(defenseTypeName);
            case "electric" -> electricOffense(defenseTypeName);
            case "psychic" -> psychicOffense(defenseTypeName);
            case "ice" -> iceOffense(defenseTypeName);
            case "dragon" -> dragonOffense(defenseTypeName);
            case "dark" -> darkOffense(defenseTypeName);
            case "fairy" -> fairyOffense(defenseTypeName);
            default -> 1f;
        };
    }

    private static float abilityCheck(PokemonEntity offense, PokemonEntity defense, float result, boolean shouldCheck) {
        //I'm not sure if they're in the proper order.
        if (!shouldCheck) {
            return result;
        }
        if (result <= 0.5f && PokemonUtils.abilityIs(offense, "tintedlens")) {
            result *= 2f;
        }
        if (result < 2f && PokemonUtils.abilityIs(defense, "wonderguard")) {
            return CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
        }
        if (PokemonUtils.abilityIs(defense, "terashell") && defense.getHealth() == defense.getMaxHealth()) {
            return CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
        }
        if (result >= 2f && PokemonUtils.abilityIs(offense, "neuroforce")) {
            result *= 1.25f;
        }
        if (result >= 2f && (PokemonUtils.abilityIs(defense, "filter") || PokemonUtils.abilityIs(defense, "solidrock") || PokemonUtils.abilityIs(defense, "prismarmor"))) {
            result *= 0.75f;
        }
        return result;
    }

    protected static float normalOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "rock", "steel" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "ghost" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float fightingOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "normal", "steel", "rock", "ice", "dark" ->
                    CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "flying", "poison", "psychic", "fairy", "bug" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "ghost" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float flyingOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "fighting", "bug", "grass" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "rock", "steel", "electric" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float poisonOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "fairy", "grass" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "poison", "ground", "rock", "ghost" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "steel" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float groundOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "poison", "rock", "steel", "fire", "electric" ->
                    CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "bug", "grass" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "flying" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float rockOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "flying", "bug", "fire", "ice" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "fighting", "ground", "steel" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float bugOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "grass", "psychic", "dark" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "fighting", "flying", "poison", "ghost", "steel", "fire", "fairy" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float ghostOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "ghost", "psychic" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "dark" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "normal" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float steelOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "rock", "ice", "fairy" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "steel", "fire", "water", "electric" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float fireOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "steel", "ice", "grass", "bug" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "rock", "fire", "water", "dragon" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float waterOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "ground", "rock", "fire" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "water", "grass", "dragon" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float grassOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "ground", "rock", "water" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "flying", "poison", "bug", "fire", "steel", "grass", "dragon" ->
                    CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float electricOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "flying", "water" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "grass", "electric", "dragon" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "ground" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float psychicOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "fighting", "poison" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "steel", "psychic" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "dark" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float iceOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "flying", "ground", "grass", "dragon" ->
                    CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "steel", "fire", "water", "ice" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float dragonOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "dragon" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "steel" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            case "fairy" -> CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
            default -> 1f;
        };
    }

    protected static float darkOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "ghost", "psychic" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "fighting", "dark", "fairy" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }

    protected static float fairyOffense(String defenseTypeName) {
        return switch (defenseTypeName) {
            case "fighting", "dragon", "dark" -> CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
            case "poison", "steel", "fire" -> CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
            default -> 1f;
        };
    }
}
