package me.rufia.fightorflight.data.behavior;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class PokemonBehaviorDataContainer {
    private final List<String> species;
    private final List<String> aspects;
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

    public PokemonBehaviorDataContainer(List<String> species, List<String> aspects, String gender, List<String> ability, List<String> move, List<String> nature, List<String> biome, String levelRequirement, String healthRatio, String lightLevel, String x, String y, String z, String distanceToPlayer, String type) {
        this.species = species;
        this.aspects = aspects;
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

    public Map<String, PokemonBehaviorData> build() {
        Map<String, PokemonBehaviorData> dataMap = new HashMap<>();
        for (String speciesName : species) {
            PokemonBehaviorData data = new PokemonBehaviorData(speciesName, aspects, gender, ability, move, nature, biome, levelRequirement, healthRatio, lightLevel, x, y, z, distanceToPlayer, type);
            dataMap.put(speciesName,data);
        }
        return dataMap;
    }
}
