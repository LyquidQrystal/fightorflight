package me.rufia.fightorflight.utils.listeners;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.behavior.PokemonBehaviorData;
import me.rufia.fightorflight.data.behavior.PokemonBehaviorDataContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Deprecated
public class BehaviorDataListener extends SimplePreparableReloadListener<Map<ResourceLocation, PokemonBehaviorDataContainer>> {
    public BehaviorDataListener() {
    }

    @Override
    protected Map<ResourceLocation, PokemonBehaviorDataContainer> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, PokemonBehaviorDataContainer> map = new HashMap<>();
        CobblemonFightOrFlight.LOGGER.info("[FOF] Preparing to read behavior data");
        prepareTag(resourceManager, "normal", PokemonBehaviorDataContainer.class, map);

        return map;
    }

    //TODO read the file
    private void prepareTag(ResourceManager resourceManager, String tagName, Type type, Map<ResourceLocation, PokemonBehaviorDataContainer> map) {
        for (var entry : resourceManager.listResources("fof_behavior_data/" + tagName, fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            var resourceLocation = entry.getKey();
            var resource = entry.getValue();
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(resource.open()));
                Gson gson = new Gson();
                map.put(resourceLocation, gson.fromJson(reader, type));
            } catch (Exception e) {
                CobblemonFightOrFlight.LOGGER.warn("Failed to read {}", resourceLocation);
            }
        }
    }

    private void register(Map<String, ? extends PokemonBehaviorData> dataMap) {
        for (var mapEntry : dataMap.entrySet()) {
            if (!(PokemonBehaviorData.behaviorData.containsKey(mapEntry.getKey()) && PokemonBehaviorData.behaviorData.get(mapEntry.getKey()) != null)) {
                PokemonBehaviorData.behaviorData.put(mapEntry.getKey(), new ArrayList<>());
            }
            PokemonBehaviorData.behaviorData.get(mapEntry.getKey()).add(mapEntry.getValue());
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, PokemonBehaviorDataContainer> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        int fileCount = 0;
        for (var entry : map.entrySet()) {
            var location = entry.getKey();
            var container = entry.getValue();
            Map<String, PokemonBehaviorData> dataMap = container.build();
            if (dataMap != null) {
                register(dataMap);
                ++fileCount;
            }
        }
        CobblemonFightOrFlight.LOGGER.info("[FOF] {} behavior data files processed.", fileCount);
    }
}
