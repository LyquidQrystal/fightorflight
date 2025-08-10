package me.rufia.fightorflight;

import com.cobblemon.mod.common.client.CobblemonClient;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import me.rufia.fightorflight.client.hud.moveslots.MoveSlotsRender;
import me.rufia.fightorflight.client.keybinds.KeybindFightOrFlight;
import me.rufia.fightorflight.client.model.PokemonSpikeModel;
import me.rufia.fightorflight.client.model.PokemonTransformingProjectileModel;
import me.rufia.fightorflight.client.renderer.*;
import me.rufia.fightorflight.entity.EntityFightOrFlight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityFightOrFlight.TRACING_BULLET.get(), PokemonTracingBulletRenderer::new);
        EntityRendererRegistry.register(EntityFightOrFlight.ARROW_PROJECTILE.get(), PokemonArrowRenderer::new);
        EntityRendererRegistry.register(EntityFightOrFlight.BULLET.get(), PokemonBulletRenderer::new);
        EntityRendererRegistry.register(EntityFightOrFlight.SPIKE.get(), PokemonSpikeRenderer::new);
        EntityRendererRegistry.register(EntityFightOrFlight.FLOATING_SPIKE.get(), PokemonSpikeRenderer::new);
        EntityRendererRegistry.register(EntityFightOrFlight.STICKY_WEB.get(), PokemonStickyWebRenderer::new);
        registerModelLayerLocation();
        for (KeyMapping keyMapping : KeybindFightOrFlight.bindings) {
            KeyBindingHelper.registerKeyBinding(keyMapping);
        }
        HudRenderCallback.EVENT.register(((drawContext, tickCounter) -> {
            if (!Minecraft.getInstance().options.hideGui) {
                var storage = CobblemonClient.INSTANCE.getStorage();
                int slot = storage.getSelectedSlot();
                var pokemon = storage.getMyParty().get(slot);
                if (pokemon != null) {
                    MoveSlotsRender.render(drawContext, tickCounter.getGameTimeDeltaPartialTick(true), pokemon);
                }
            }
        }));
    }

    public void registerModelLayerLocation() {
        EntityModelLayerRegistry.register(PokemonSpikeModel.LAYER_LOCATION, PokemonSpikeModel::createBodyLayer);
        EntityModelLayerRegistry.register(PokemonTransformingProjectileModel.LAYER_LOCATION, PokemonTransformingProjectileModel::createBodyLayer);
    }
}
