package me.rufia.fightorflight.utils.listeners;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.behavior.PokemonBehaviorData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class BehaviorDataListener extends SimplePreparableReloadListener<Map<ResourceLocation, PokemonBehaviorData>> {
    public BehaviorDataListener() {
    }
    @Override
    protected Map<ResourceLocation, PokemonBehaviorData> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, PokemonBehaviorData> map = new HashMap<>();
        CobblemonFightOrFlight.LOGGER.info("[FOF] Preparing to read behavior data");
        //prepareTag(resourceManager, "stat", StatChangeMoveDataContainer.class, map);

        return map;
    }

    private void prepareTag(ResourceManager resourceManager, String tagName, Type type, Map<ResourceLocation, PokemonBehaviorData> map) {
        for (var entry : resourceManager.listResources("fof_behavior_data/" + tagName, fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            var resourceLocation = entry.getKey();
            var resource = entry.getValue();
            try {
                //CobblemonFightOrFlight.LOGGER.info(resourceLocation.getPath());
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

        }
    }

    @Override
    protected void apply(Map<ResourceLocation, PokemonBehaviorData> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        int fileCount = 0;
        for (var entry : map.entrySet()) {
            var location = entry.getKey();
            var data = entry.getValue();
            Map<String, ? extends PokemonBehaviorData> dataMap = null;
            /*
            if (container instanceof StatChangeMoveDataContainer statChangeMoveDataContainer) {
                dataMap = statChangeMoveDataContainer.build();
            }*/
            if (dataMap != null) {
                register(dataMap);
                ++fileCount;
            }
        }
        CobblemonFightOrFlight.LOGGER.info("[FOF] {} move data files processed.", fileCount);
    }
}
