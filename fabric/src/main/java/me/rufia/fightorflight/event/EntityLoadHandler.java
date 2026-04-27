package me.rufia.fightorflight.event;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class EntityLoadHandler implements ServerEntityEvents.Load {

    @Override
    public void onLoad(Entity entity, ServerLevel world) {
        if (entity instanceof PokemonEntity){
            //CobblemonFightOrFlight.addPokemonGoal((PokemonEntity) entity);
        }
    }
}
