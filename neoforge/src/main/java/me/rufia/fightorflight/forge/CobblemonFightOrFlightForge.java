package me.rufia.fightorflight.forge;


import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.effects.FOFEffects;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.platform.neoforge.EffectRegisterImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;


@Mod(CobblemonFightOrFlight.MODID)
public final class CobblemonFightOrFlightForge {
    public CobblemonFightOrFlightForge(IEventBus modEventBus, ModContainer modContainer) {
        CobblemonFightOrFlight.LOGGER.info("Hello neoforge from Fight or Flight");
        EntityFightOrFlight.bootstrap();
        ItemFightOrFlight.bootstrap();
        FOFEffects.bootstrap();
        EffectRegisterImpl.MOB_EFFECTS.register(modEventBus);
        CobblemonFightOrFlight.init((pokemonEntity, priority, goal) -> pokemonEntity.goalSelector.addGoal(priority, goal));
        NeoForge.EVENT_BUS.addListener(ForgeBusEvent::onEntityJoined);

    }
}
