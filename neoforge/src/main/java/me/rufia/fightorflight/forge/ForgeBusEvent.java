package me.rufia.fightorflight.forge;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;


public class ForgeBusEvent {
    @SubscribeEvent
    public static void onEntityJoined(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PokemonEntity pokemonEntity) {
            //CobblemonFightOrFlight.addPokemonGoal(pokemonEntity);
        }
    }
}
