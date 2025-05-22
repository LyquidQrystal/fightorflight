package me.rufia.fightorflight.data.behavior;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.signednumber.SignedFloat;
import me.rufia.fightorflight.utils.signednumber.SignedInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class PokemonBehaviorData {
    public static final Map<String, List<PokemonBehaviorData>> behaviorData = new HashMap<>();
    private final String species;
    private final List<String> aspects;
    private final String gender;
    private final List<String> biome;
    private final List<String> ability;
    private final List<String> moves;
    private final List<String> nature;
    private final String levelRequirement;
    private final String healthRatio;
    private final String lightLevel;
    private final String x;
    private final String y;
    private final String z;
    private final String distanceToPlayer;
    private final String type;

    public PokemonBehaviorData(String species, List<String> aspects, String gender, List<String> ability, List<String> move, List<String> nature, List<String> biome, String levelRequirement, String healthRatio, String lightLevel, String x, String y, String z, String distanceToPlayer, String type) {
        this.species = species;
        this.aspects = aspects;
        this.ability = ability;
        this.gender = gender;
        this.moves = move;
        this.nature = nature;
        this.biome = biome;
        this.levelRequirement = levelRequirement;
        this.healthRatio = healthRatio;
        this.lightLevel = lightLevel;
        this.x = x;
        this.y = y;
        this.z = z;
        this.distanceToPlayer = distanceToPlayer;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean check(LivingEntity entity, PokemonEntity pokemonEntity) {
        if (entity != null && pokemonEntity != null) {
            return check(pokemonEntity) && checkDTP(entity, pokemonEntity);
        }
        return false;
    }

    public boolean check(PokemonEntity pokemonEntity) {
        if (pokemonEntity != null) {
            return check(pokemonEntity.getPokemon())
                    && checkBiome(pokemonEntity)
                    && checkLightLevel(pokemonEntity)
                    && checkHealthRatio(pokemonEntity)
                    && checkMove(pokemonEntity)
                    && checkX(pokemonEntity)
                    && checkY(pokemonEntity)
                    && checkZ(pokemonEntity);
        }
        return false;
    }

    public boolean check(Pokemon pokemon) {
        if (pokemon == null) {
            return false;
        }
        return checkItem(species, pokemon.getSpecies().getName())
                && checkItem(gender, pokemon.getGender().toString())
                && checkItem(nature, PokemonUtils.getNatureName(pokemon))
                && checkItem(ability, pokemon.getAbility().getName())
                && checkLevel(pokemon)
                && checkAspects(aspects, pokemon.getAspects());
    }

    private boolean checkItem(String targetData, String pokemonData) {
        if (!targetData.isEmpty()) {
            return Objects.equals(targetData.toLowerCase(), pokemonData.toLowerCase());
        }
        return true;
    }

    private boolean checkItem(List<String> targetData, String pokemonData) {
        if (!targetData.isEmpty()) {
            return targetData.contains(pokemonData);
        }
        return true;
    }

    private boolean checkAspects(List<String> requiredAspects, Set<String> pokemonAspects) {
        for (String aspect : requiredAspects) {
            if (!pokemonAspects.contains(aspect)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkLevel(Pokemon pokemon) {
        SignedInt signedInt = new SignedInt();
        if (signedInt.load(levelRequirement)) {
            int pokemonLvl = pokemon.getLevel();
            return signedInt.check(pokemonLvl);
        }
        return levelRequirement.isEmpty();
    }

    private boolean checkMove(PokemonEntity pokemonEntity) {
        if (moves.isEmpty()) {
            return true;
        }
        var moveSet = pokemonEntity.getPokemon().getMoveSet();
        for (Move m : moveSet) {
            String moveName = m.getName();
            if (moves.contains(moveName)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHealthRatio(PokemonEntity pokemonEntity) {
        SignedFloat signedFloat = new SignedFloat();
        if (signedFloat.load(healthRatio)) {
            float ratio = pokemonEntity.getHealth() / pokemonEntity.getMaxHealth();
            return signedFloat.check(ratio);
        }
        return healthRatio.isEmpty();
    }

    private boolean checkLightLevel(PokemonEntity pokemonEntity) {
        SignedInt signedInt = new SignedInt();
        if (signedInt.load(lightLevel)) {
            int light = pokemonEntity.level().getRawBrightness(pokemonEntity.blockPosition(), pokemonEntity.level().getSkyDarken());
            return signedInt.check(light);
        }
        return lightLevel.isEmpty();
    }

    private boolean checkX(PokemonEntity pokemonEntity) {
        SignedFloat signedFloat = new SignedFloat();
        if (signedFloat.load(x)) {
            return signedFloat.check(pokemonEntity.getX());
        }
        return x.isEmpty();
    }

    private boolean checkY(PokemonEntity pokemonEntity) {
        SignedFloat signedFloat = new SignedFloat();
        if (signedFloat.load(y)) {
            return signedFloat.check(pokemonEntity.getY());
        }
        return y.isEmpty();
    }

    private boolean checkZ(PokemonEntity pokemonEntity) {
        SignedFloat signedFloat = new SignedFloat();
        if (signedFloat.load(z)) {
            return signedFloat.check(pokemonEntity.getZ());
        }
        return z.isEmpty();
    }

    //Distance to player
    private boolean checkDTP(Entity entity, PokemonEntity pokemonEntity) {
        SignedFloat signedFloat = new SignedFloat();
        if (signedFloat.load(distanceToPlayer)) {
            return signedFloat.check(entity.distanceTo(pokemonEntity));
        }
        return distanceToPlayer.isEmpty();
    }

    private boolean checkBiome(PokemonEntity pokemonEntity) {
        return biome.contains(pokemonEntity.level().getBiome(pokemonEntity.blockPosition()).getRegisteredName());
    }
}
