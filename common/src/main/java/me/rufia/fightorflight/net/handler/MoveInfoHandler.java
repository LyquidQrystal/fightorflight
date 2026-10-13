package me.rufia.fightorflight.net.handler;

import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.CobblemonFightOrFlightClient;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.MoveInfoPacket;
import me.rufia.fightorflight.utils.msg.FOFMsg;
import me.rufia.fightorflight.utils.msg.FOFSwitchMoveMsg;
import net.minecraft.client.Minecraft;

public class MoveInfoHandler implements NetworkPacketHandler<MoveInfoPacket> {
    @Override
    public void handle(MoveInfoPacket packet, NetworkManager.PacketContext context) {
        if (!CobblemonFightOrFlight.visualEffectConfig().show_switch_move_msg_client) {
            return;
        }
        int guiTick = Minecraft.getInstance().gui.getGuiTicks();
        FOFMsg msg = new FOFSwitchMoveMsg(packet, guiTick);
        CobblemonFightOrFlightClient.addMsg(msg);
    }
}
