package me.rufia.fightorflight;


import me.rufia.fightorflight.effects.FOFEffects;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.entity.ai.sensors.FOFSensors;
import me.rufia.fightorflight.event.EntityLoadHandler;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.mixin.MobEntityAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;


public final class CobblemonFightOrFlightFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonFightOrFlight.LOGGER.info("Hello Fabric world from Fight or Flight!");
        EntityFightOrFlight.bootstrap();
        ItemFightOrFlight.bootstrap();
        FOFEffects.bootstrap();
        CobblemonFightOrFlight.init((pokemonEntity, priority, goal) -> ((MobEntityAccessor) pokemonEntity).goalSelector().addGoal(priority, goal));
        ServerEntityEvents.ENTITY_LOAD.register(new EntityLoadHandler());
        FOFSensors.sensors.forEach((key, sensorType) -> {
            Registry.register(BuiltInRegistries.SENSOR_TYPE, ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.COBBLEMON_MOD_ID, key), sensorType);
        });
    }
}