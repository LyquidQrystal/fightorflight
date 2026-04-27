package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.client.CobblemonClient;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.FOFStartBattlePokemonPacket;
import me.rufia.fightorflight.net.packet.FOFStartBattleRequestPacket;
import net.minecraft.world.entity.player.Player;

public class FOFStartBattleRequestHandler implements NetworkPacketHandler<FOFStartBattleRequestPacket> {

    @Override
    public void handle(FOFStartBattleRequestPacket packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        if (player.level().isClientSide) {
            int selectedSlot = CobblemonClient.INSTANCE.getStorage().getSelectedSlot();
            var pokemon = CobblemonClient.INSTANCE.getStorage().getParty().get(selectedSlot);
            if (pokemon == null) {
                return;
            }
            FOFStartBattlePokemonPacket startBattlePacket = new FOFStartBattlePokemonPacket(packet.getPokemonEntityID(), pokemon.getUuid());
            NetworkManager.sendToServer(startBattlePacket);
        }
    }
}
