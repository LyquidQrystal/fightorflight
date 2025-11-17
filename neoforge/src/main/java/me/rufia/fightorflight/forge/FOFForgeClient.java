package me.rufia.fightorflight.forge;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.keybinds.KeybindFightOrFlight;
import me.rufia.fightorflight.client.model.*;
import me.rufia.fightorflight.client.renderer.*;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CobblemonFightOrFlight.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class FOFForgeClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityFightOrFlight.TRACING_BULLET.get(), PokemonTracingBulletRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.ARROW_PROJECTILE.get(), PokemonArrowRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.BULLET.get(), PokemonBulletRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.SPIKE.get(), PokemonSpikeRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.FLOATING_SPIKE.get(), PokemonSpikeRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.STICKY_WEB.get(), PokemonStickyWebRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.TORNADO.get(), PokemonAreaEffectTornadoRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.WHIRLPOOL.get(), PokemonAreaEffectWhirlpoolRenderer::new);
        EntityRenderers.register(EntityFightOrFlight.MAGIC_EFFECT.get(), PokemonAreaEffectMagicRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        KeybindFightOrFlight.bindings.forEach(event::register);
    }

    @SubscribeEvent
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PokemonSpikeModel.LAYER_LOCATION, PokemonSpikeModel::createBodyLayer);
        event.registerLayerDefinition(PokemonTransformingProjectileModel.LAYER_LOCATION, PokemonTransformingProjectileModel::createBodyLayer);
        event.registerLayerDefinition(PokemonAreaEffectTornadoModel.LAYER_LOCATION, PokemonAreaEffectTornadoModel::createBodyLayer);
        event.registerLayerDefinition(PokemonAreaEffectWhirlpoolModel.LAYER_LOCATION, PokemonAreaEffectWhirlpoolModel::createBodyLayer);
        event.registerLayerDefinition(PokemonAreaEffectMagicModel.LAYER_LOCATION, PokemonAreaEffectMagicModel::createBodyLayer);
    }
}