package me.rufia.fightorflight.utils.listeners;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.effectiveness.FOFTypeEffectiveness;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TypeEffectivenessListener extends SimplePreparableReloadListener<Map<ResourceLocation, FOFTypeEffectiveness>> {
    public TypeEffectivenessListener() {
    }

    @Override
    protected Map<ResourceLocation, FOFTypeEffectiveness> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, FOFTypeEffectiveness> map = new HashMap<>();
        CobblemonFightOrFlight.LOGGER.info("[FOF] Preparing to read custom type effectiveness data");
        for (var entry : resourceManager.listResources("fof_move_data", fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            var resourceLocation = entry.getKey();
            var resource = entry.getValue();
            try {
                //CobblemonFightOrFlight.LOGGER.info(resourceLocation.getPath());
                JsonReader reader = new JsonReader(new InputStreamReader(resource.open()));
                Gson gson = new Gson();
                map.put(resourceLocation, gson.fromJson(reader, FOFTypeEffectiveness.class));
            } catch (Exception e) {
                CobblemonFightOrFlight.LOGGER.warn("Failed to read {}", resourceLocation);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, FOFTypeEffectiveness> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        FOFTypeEffectiveness.TYPE_EFFECTIVENESS.clear();
    }
}
