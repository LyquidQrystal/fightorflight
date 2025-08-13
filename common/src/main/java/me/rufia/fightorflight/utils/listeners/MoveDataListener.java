package me.rufia.fightorflight.utils.listeners;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.data.movedata.MoveDataContainer;
import me.rufia.fightorflight.data.movedata.container.MiscMoveDataContainer;
import me.rufia.fightorflight.data.movedata.container.StatChangeMoveDataContainer;
import me.rufia.fightorflight.data.movedata.container.StatusEffectMoveDataContainer;
import me.rufia.fightorflight.utils.FOFUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveDataListener extends SimplePreparableReloadListener<Map<ResourceLocation, MoveDataContainer>> {
    public MoveDataListener() {
    }

    @Override
    protected Map<ResourceLocation, MoveDataContainer> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, MoveDataContainer> map = new HashMap<>();
        CobblemonFightOrFlight.LOGGER.info("[FOF] Preparing to read move data");
        prepareTag(resourceManager, "stat", StatChangeMoveDataContainer.class, map);
        prepareTag(resourceManager, "status", StatusEffectMoveDataContainer.class, map);
        prepareTag(resourceManager, "misc", MiscMoveDataContainer.class, map);
        return map;
    }

    private void prepareTag(ResourceManager resourceManager, String tagName, Type type, Map<ResourceLocation, MoveDataContainer> map) {
        for (var entry : resourceManager.listResources("fof_move_data/" + tagName, fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
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

    private void register(Map<String, ? extends MoveData> dataMap) {
        for (var mapEntry : dataMap.entrySet()) {
            FOFUtils.registerMoveData(mapEntry.getKey(), mapEntry.getValue());
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, MoveDataContainer> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        MoveData.moveData.clear();
        int fileCount = 0;
        for (var entry : map.entrySet()) {
            var location = entry.getKey();
            var container = entry.getValue();
            Map<String, ? extends MoveData> dataMap = null;
            if (container instanceof StatChangeMoveDataContainer statChangeMoveDataContainer) {
                dataMap = statChangeMoveDataContainer.build();
            } else if (container instanceof StatusEffectMoveDataContainer statusEffectMoveDataContainer) {
                dataMap = statusEffectMoveDataContainer.build();
            } else if (container instanceof MiscMoveDataContainer miscMoveDataContainer) {
                dataMap = miscMoveDataContainer.build();
            }
            if (dataMap != null) {
                register(dataMap);
                ++fileCount;
            }
        }
        CobblemonFightOrFlight.LOGGER.info("[FOF] {} move data files processed.", fileCount);
        CobblemonFightOrFlight.fromConfigToMoveData();
    }
}
