package me.rufia.fightorflight.data.behavior;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.utils.PokemonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final String type;
    private final float value;

    public PokemonBehaviorData(List<String> species, String form, String gender, List<String> ability, List<String> move, List<String> nature, List<String> biome, String levelRequirement, String type, float value) {
        this.species = species;
        this.form = form;
        this.ability = ability;
        this.gender = gender;
        this.move = move;
        this.nature = nature;
        this.biome = biome;
        this.levelRequirement = levelRequirement;
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public boolean check(PokemonEntity pokemonEntity) {
        if (pokemonEntity != null) {
            return check(pokemonEntity.getPokemon()) && checkBiome(pokemonEntity);
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
        String pattern = "([<>=])(\\d+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(levelRequirement);
        if (m.find()) {
            try {
                if (m.groupCount() == 2) {
                    int lvl = Integer.parseInt(m.group(2));
                    int pokemonLvl = pokemon.getLevel();
                    String operator = m.group(1);
                    if (Objects.equals(operator, "=")) {
                        return pokemonLvl == lvl;
                    } else if (Objects.equals(operator, "<")) {
                        return pokemonLvl < lvl;
                    } else if (Objects.equals(operator, ">")) {
                        return pokemonLvl > lvl;
                    }
                }
            } catch (NumberFormatException e) {
                CobblemonFightOrFlight.LOGGER.warn("Failed to convert the level requirement in the datapack");
                return true;
            }
        }
        return levelRequirement.isEmpty();
    }

    private boolean checkBiome(PokemonEntity pokemonEntity){
        return biome.contains(pokemonEntity.level().getBiome(pokemonEntity.blockPosition()).getRegisteredName());
    }
}
