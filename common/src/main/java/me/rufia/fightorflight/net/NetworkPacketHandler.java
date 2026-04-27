package me.rufia.fightorflight.net;

import dev.architectury.networking.NetworkManager;

public interface NetworkPacketHandler<T extends NetworkPacket> {
    void handle(T packet, NetworkManager.PacketContext context);
}
