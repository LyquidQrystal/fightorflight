package me.rufia.fightorflight.data.behavior;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.signednumber.SignedFloat;
import me.rufia.fightorflight.utils.signednumber.SignedInt;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class PokemonBehaviorData {
    public static final Map<String, List<MoveData>> behaviorData = new HashMap<>();
    private final List<String> species;
    private final String form;
    private final String gender;
    private final List<String> biome;
    private final List<String> ability;
    private final List<String> move;
    private final List<String> nature;
    private final String levelRequirement;
    private final String healthRatio;
    private final String lightLevel;
    private final String x;
    private final String y;
    private final String z;
    private final String distanceToPlayer;
    private final String type;

    public PokemonBehaviorData(List<String> species, String form, String gender, List<String> ability, List<String> move, List<String> nature, List<String> biome, String levelRequirement, String healthRatio, String lightLevel, String x, String y, String z, String distanceToPlayer, String type) {
        this.species = species;
        this.form = form;
        this.ability = ability;
        this.gender = gender;
        this.move = move;
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

    public boolean check(Entity entity, PokemonEntity pokemonEntity) {
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
                && checkItem(form, pokemon.getForm().getName())
                && checkItem(gender, pokemon.getGender().toString())
                && checkItem(nature, PokemonUtils.getNatureName(pokemon))
                && checkItem(ability, pokemon.getAbility().getName())
                && checkLevel(pokemon);
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

    private boolean checkLevel(Pokemon pokemon) {
        SignedInt signedInt = new SignedInt();
        if (signedInt.load(levelRequirement)) {
            int pokemonLvl = pokemon.getLevel();
            return signedInt.check(pokemonLvl);
        }
        return levelRequirement.isEmpty();
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
