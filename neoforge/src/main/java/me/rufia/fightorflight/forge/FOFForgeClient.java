package me.rufia.fightorflight.forge;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.keybinds.KeybindFightOrFlight;
import me.rufia.fightorflight.client.renderer.PokemonArrowRenderer;
import me.rufia.fightorflight.client.renderer.PokemonBulletRenderer;
import me.rufia.fightorflight.client.renderer.PokemonSpikeRenderer;
import me.rufia.fightorflight.client.renderer.PokemonTracingBulletRenderer;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CobblemonFightOrFlight.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class FOFForgeClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityFightOrFlight.TRACING_BULLET.get(), PokemonTracingBulletRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.ARROW_PROJECTILE.get(), PokemonArrowRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.BULLET.get(), PokemonBulletRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.SPIKE.get(), PokemonSpikeRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        KeybindFightOrFlight.bindings.forEach(event::register);
    }
}