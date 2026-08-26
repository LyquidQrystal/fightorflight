package me.rufia.fightorflight.utils;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.effectiveness.FOFTypeEffectiveness;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeEffectiveness {
    public static float calcTypeEffectiveness(PokemonEntity offense, PokemonEntity defense) {
        Effectiveness effectiveness = new Effectiveness();
        return calcTypeEffectiveness(offense, defense, true, effectiveness);
    }

    public static float calcTypeEffectivenessSimple(String typeName, LivingEntity defending) {
        Effectiveness effectiveness = new Effectiveness();
        getTypeEffectivenessSimple(typeName, defending, effectiveness, false);
        return effectiveness.getResult();
    }

    public static float calcTypeEffectivenessDefenseNoPKM(PokemonEntity offense, ElementalType defendType) {
        Effectiveness effectiveness = new Effectiveness();
        return calcTypeEffectivenessDefenseNoPKM(offense, defendType, true, effectiveness);
    }

    protected static float calcTypeEffectiveness(PokemonEntity offense, LivingEntity defense, boolean shouldCheckAbility, Effectiveness effectiveness) {
        if (!CobblemonFightOrFlight.commonConfig().type_effectiveness_between_pokemon && defense instanceof PokemonEntity) {
            return 1f;
        }
        Move move = PokemonUtils.getMove(offense);

        if (move != null) {
            var el = getTargetElementalType(defense, PokemonUtils.isMoldBreakerLike(offense));
            for (ElementalType e : el) {
                getMoveTypeEffectiveness(move, e, effectiveness);
            }
        } else {
            ElementalType offenseType = offense.getPokemon().getPrimaryType();
            getTypeEffectivenessSimple(offenseType.getName(), defense, effectiveness, PokemonUtils.isMoldBreakerLike(offense));
        }
        applyCustomTypeEffectiveness(offense, defense, effectiveness, shouldCheckAbility && PokemonUtils.isMoldBreakerLike(offense));
        return abilityCheck(offense, defense, effectiveness, shouldCheckAbility);
    }

    protected static float calcTypeEffectivenessDefenseNoPKM(PokemonEntity offense, ElementalType defendType, boolean shouldCheckAbility, Effectiveness effectiveness) {
        Move move = PokemonUtils.getMove(offense);

        if (move != null) {
            getMoveTypeEffectiveness(move, defendType, effectiveness);
        } else {
            ElementalType offenseType = offense.getPokemon().getPrimaryType();
            getTypeEffectiveness(offenseType.getName(), defendType.getName(), effectiveness);
        }

        return abilityCheck(offense, null, effectiveness, shouldCheckAbility);
    }

    protected static void getMoveTypeEffectiveness(Move offenseMove, ElementalType defenseType, Effectiveness effectiveness) {
        if (offenseMove.getName().equals("freezedry")) {
            if (defenseType.getName().equals("Water")) {
                effectiveness.update(1, false);
            }
        }
        getTypeEffectiveness(offenseMove.getType().getName(), defenseType.getName(), effectiveness);
        if (offenseMove.getName().equals("flyingpress")) {
            getTypeEffectiveness("Flying", defenseType.getName(), effectiveness);
        }
    }

    protected static void getTypeEffectivenessSimple(String typeName, LivingEntity defending, Effectiveness effectiveness, boolean moldBreakerAvailable) {
        var el = getTargetElementalType(defending, moldBreakerAvailable);
        for (ElementalType e : el) {
            getTypeEffectiveness(typeName, e.getName(), effectiveness);
        }
    }

    protected static Set<ElementalType> getTargetElementalType(LivingEntity target, boolean moldBreakerAvailable) {
        Set<ElementalType> result = new HashSet<>();
        if (target instanceof PokemonEntity pokemonEntity) {
            result.add(pokemonEntity.getPokemon().getPrimaryType());
            var secType = pokemonEntity.getPokemon().getSecondaryType();
            if (secType != null) {
                result.add(secType);
            }
        } else {
            if (FOFTypeEffectiveness.TYPE_EFFECTIVENESS.containsKey(target.getEncodeId())) {
                var l = FOFTypeEffectiveness.TYPE_EFFECTIVENESS.get(target.getEncodeId());
                for (FOFTypeEffectiveness te : l) {
                    if (!moldBreakerAvailable && te.isIgnoredByMoldBreaker()) {
                        List<String> ls = te.getAttachedElementalType();
                        for (String st : ls) {
                            ElementalType e = ElementalTypes.get(st);
                            if (e != null) {
                                result.add(e);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    protected static void getTypeEffectiveness(String offenseTypeName, String defenseTypeName, Effectiveness effectiveness) {
        String offenseTypeNameLower = offenseTypeName.toLowerCase();
        String defenseTypeNameLower = defenseTypeName.toLowerCase();
        switch (offenseTypeNameLower) {
            case "normal":
                normalOffense(defenseTypeNameLower, effectiveness);
                break;
            case "fighting":
                fightingOffense(defenseTypeNameLower, effectiveness);
                break;
            case "flying":
                flyingOffense(defenseTypeNameLower, effectiveness);
                break;
            case "poison":
                poisonOffense(defenseTypeNameLower, effectiveness);
                break;
            case "ground":
                groundOffense(defenseTypeNameLower, effectiveness);
                break;
            case "rock":
                rockOffense(defenseTypeNameLower, effectiveness);
                break;
            case "bug":
                bugOffense(defenseTypeNameLower, effectiveness);
                break;
            case "ghost":
                ghostOffense(defenseTypeNameLower, effectiveness);
                break;
            case "steel":
                steelOffense(defenseTypeNameLower, effectiveness);
                break;
            case "fire":
                fireOffense(defenseTypeNameLower, effectiveness);
                break;
            case "water":
                waterOffense(defenseTypeNameLower, effectiveness);
                break;
            case "grass":
                grassOffense(defenseTypeNameLower, effectiveness);
                break;
            case "electric":
                electricOffense(defenseTypeNameLower, effectiveness);
                break;
            case "psychic":
                psychicOffense(defenseTypeNameLower, effectiveness);
                break;
            case "ice":
                iceOffense(defenseTypeNameLower, effectiveness);
                break;
            case "dragon":
                dragonOffense(defenseTypeNameLower, effectiveness);
                break;
            case "dark":
                darkOffense(defenseTypeNameLower, effectiveness);
                break;
            case "fairy":
                fairyOffense(defenseTypeNameLower, effectiveness);
                break;
            default:
                break;
        }
    }

    protected static void applyCustomTypeEffectiveness(PokemonEntity offense, LivingEntity target, Effectiveness effectiveness, boolean moldBreakerAvailable) {
        if (target instanceof PokemonEntity) {
            return;
        }
        //TODO
    }

    private static float abilityCheck(PokemonEntity offense, LivingEntity defense, Effectiveness effectiveness, boolean shouldCheck) {
        //I'm not sure if they're in the proper order.
        float result = effectiveness.getResult();
        if (!shouldCheck) {
            return result;
        }
        result *= offensiveAbilityMultiplier(offense, effectiveness);
        if (defense instanceof PokemonEntity defendingPokemon) {
            result *= defensiveAbilityMultiplier(defendingPokemon, PokemonUtils.isMoldBreakerLike(offense), effectiveness);
        }
        if (effectiveness.isNoEffect()) {
            return getNoEffectMultiplier();
        }
        return result;
    }

    private static float offensiveAbilityMultiplier(PokemonEntity offense, Effectiveness effectiveness) {
        if (effectiveness.getStage() < 0 && PokemonUtils.abilityIs(offense, "tintedlens")) {
            return 2f;
        }
        if (effectiveness.getStage() > 0 && PokemonUtils.abilityIs(offense, "neuroforce")) {
            return 1.25f;
        }
        return 1f;
    }

    private static float defensiveAbilityMultiplier(PokemonEntity defense, boolean isMoldBreaker, Effectiveness effectiveness) {
        if (effectiveness.getStage() < 1 && PokemonUtils.abilityIs(defense, "wonderguard") && !isMoldBreaker) {
            effectiveness.hitNoEffect();
        }
        if (PokemonUtils.abilityIs(defense, "terashell") && defense.getHealth() == defense.getMaxHealth()) {
            return getNotVeryEffectiveMultiplier();
        }
        if (effectiveness.getStage() > 0 && (PokemonUtils.abilityIs(defense, "filter") || PokemonUtils.abilityIs(defense, "solidrock") || PokemonUtils.abilityIs(defense, "prismarmor"))) {
            return 0.75f;
        }
        return 1f;
    }

    protected static void normalOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "rock", "steel":
                effectiveness.hitResistance();
                break;
            case "ghost":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void fightingOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "normal", "steel", "rock", "ice", "dark":
                effectiveness.hitWeakness();
                break;
            case "flying", "poison", "psychic", "fairy", "bug":
                effectiveness.hitResistance();
                break;
            case "ghost":
                effectiveness.hitNoEffect();
            default:
                break;
        }
    }

    protected static void flyingOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "fighting", "bug", "grass":
                effectiveness.hitWeakness();
                break;
            case "rock", "steel", "electric":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void poisonOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "fairy", "grass":
                effectiveness.hitWeakness();
                break;
            case "poison", "ground", "rock", "ghost":
                effectiveness.hitResistance();
                break;
            case "steel":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void groundOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "poison", "rock", "steel", "fire", "electric":
                effectiveness.hitWeakness();
                break;
            case "bug", "grass":
                effectiveness.hitResistance();
                break;
            case "flying":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void rockOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "flying", "bug", "fire", "ice":
                effectiveness.hitWeakness();
                break;
            case "fighting", "ground", "steel":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void bugOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "grass", "psychic", "dark":
                effectiveness.hitWeakness();
                break;
            case "fighting", "flying", "poison", "ghost", "steel", "fire", "fairy":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void ghostOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "ghost", "psychic":
                effectiveness.hitWeakness();
                break;
            case "dark":
                effectiveness.hitResistance();
                break;
            case "normal":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void steelOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "rock", "ice", "fairy":
                effectiveness.hitWeakness();
                break;
            case "steel", "fire", "water", "electric":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void fireOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "steel", "ice", "grass", "bug":
                effectiveness.hitWeakness();
                break;
            case "rock", "fire", "water", "dragon":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void waterOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "ground", "rock", "fire":
                effectiveness.hitWeakness();
                break;
            case "water", "grass", "dragon":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void grassOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "ground", "rock", "water":
                effectiveness.hitWeakness();
                break;
            case "flying", "poison", "bug", "fire", "steel", "grass", "dragon":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void electricOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "flying", "water":
                effectiveness.hitWeakness();
                break;
            case "grass", "electric", "dragon":
                effectiveness.hitResistance();
                break;
            case "ground":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void psychicOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "fighting", "poison":
                effectiveness.hitWeakness();
                break;
            case "steel", "psychic":
                effectiveness.hitResistance();
                break;
            case "dark":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void iceOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "flying", "ground", "grass", "dragon":
                effectiveness.hitWeakness();
                break;
            case "steel", "fire", "water", "ice":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void dragonOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "dragon":
                effectiveness.hitWeakness();
                break;
            case "steel":
                effectiveness.hitResistance();
                break;
            case "fairy":
                effectiveness.hitNoEffect();
                break;
            default:
                break;
        }
    }

    protected static void darkOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "ghost", "psychic":
                effectiveness.hitWeakness();
                break;
            case "fighting", "dark", "fairy":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    protected static void fairyOffense(String defenseTypeName, Effectiveness effectiveness) {
        switch (defenseTypeName) {
            case "fighting", "dragon", "dark":
                effectiveness.hitWeakness();
                break;
            case "poison", "steel", "fire":
                effectiveness.hitResistance();
                break;
            default:
                break;
        }
    }

    public static float getSuperEffectiveMultiplier() {
        return CobblemonFightOrFlight.commonConfig().super_effective_multiplier;
    }

    public static float getNotVeryEffectiveMultiplier() {
        return CobblemonFightOrFlight.commonConfig().not_very_effective_multiplier;
    }

    public static float getNoEffectMultiplier() {
        return CobblemonFightOrFlight.commonConfig().no_effect_multiplier;
    }

    protected static class Effectiveness {
        private int stage = 0;
        private boolean isNoEffect = false;

        public int getStage() {
            return stage;
        }

        public boolean isNoEffect() {
            return isNoEffect;
        }

        public void update(int stageChange, boolean isNoEffect) {
            stage += stageChange;
            if (isNoEffect) {
                this.isNoEffect = true;
            }
        }

        public void hitWeakness() {
            update(1, false);
        }

        public void hitResistance() {
            update(-1, false);
        }

        public void hitNoEffect() {
            update(0, true);
        }

        public float getResult() {
            if (isNoEffect) {
                return getNoEffectMultiplier();
            }
            if (stage != 0) {
                return (float) (stage > 0 ? Math.pow(getSuperEffectiveMultiplier(), stage) : Math.pow(getNotVeryEffectiveMultiplier(), -stage));
            }
            return 1f;
        }
    }
}
