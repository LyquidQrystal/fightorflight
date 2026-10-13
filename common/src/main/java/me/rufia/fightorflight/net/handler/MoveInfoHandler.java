package me.rufia.fightorflight.net.handler;

import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.CobblemonFightOrFlightClient;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.MoveInfoPacket;
import me.rufia.fightorflight.utils.msg.FOFMsg;
import me.rufia.fightorflight.utils.msg.FOFSwitchMoveMsg;
import net.minecraft.client.Minecraft;

public class MoveInfoHandler implements NetworkPacketHandler<MoveInfoPacket> {
    @Override
    public void handle(MoveInfoPacket packet, NetworkManager.PacketContext context) {
        int guiTick=Minecraft.getInstance().gui.getGuiTicks();
        FOFMsg msg = new FOFSwitchMoveMsg(packet,guiTick);
        CobblemonFightOrFlightClient.addMsg(msg);
    }
}
