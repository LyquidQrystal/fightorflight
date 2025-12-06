package me.rufia.fightorflight.forge;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.sensors.FOFSensors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;


public class ForgeBusEvent {
    @SubscribeEvent
    public static void onEntityJoined(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PokemonEntity pokemonEntity) {
            //CobblemonFightOrFlight.addPokemonGoal(pokemonEntity);
        }
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.SENSOR_TYPE, registry -> {
            FOFSensors.sensors.forEach((key, sensorType) -> {
                registry.register(ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, key), sensorType);
            });
        });
    }
}
