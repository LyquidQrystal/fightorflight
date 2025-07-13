package me.rufia.fightorflight;

import me.rufia.fightorflight.compat.LivelierPokemonCompat;
import me.rufia.fightorflight.effects.FOFEffects;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.event.EntityLoadHandler;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.mixin.MobEntityAccessor;
import necro.livelier.pokemon.common.config.CategoryCache;
import necro.livelier.pokemon.common.config.CategoryConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.FabricLoader;


public final class CobblemonFightOrFlightFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CobblemonFightOrFlight.LOGGER.info("Hello Fabric world from Fight or Flight!");
        EntityFightOrFlight.bootstrap();
        ItemFightOrFlight.bootstrap();
        FOFEffects.bootstrap();
        CobblemonFightOrFlight.init((pokemonEntity, priority, goal) -> ((MobEntityAccessor) pokemonEntity).goalSelector().addGoal(priority, goal));
        ServerEntityEvents.ENTITY_LOAD.register(new EntityLoadHandler());
        if (FabricLoader.getInstance().isModLoaded(LivelierPokemonCompat.getModID())) {
            LivelierPokemonCompat.load(true);
        }
    }
}