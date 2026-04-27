package me.rufia.fightorflight.forge;

import com.cobblemon.mod.common.client.CobblemonClient;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.hud.moveslots.MoveSlotsRender;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = CobblemonFightOrFlight.MODID, value = Dist.CLIENT)
public class ClientBusEvent {
    @SubscribeEvent
    public static void onRenderHud(RenderGuiEvent.Post event) {
        if (!Minecraft.getInstance().options.hideGui) {
            var storage = CobblemonClient.INSTANCE.getStorage();
            int slot = storage.getSelectedSlot();
            var pokemon = storage.getParty().get(slot);
            if (pokemon != null) {
                MoveSlotsRender.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaPartialTick(true), pokemon);
            }
        }
    }
}
