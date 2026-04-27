package me.rufia.fightorflight.net.packet;

import me.rufia.fightorflight.net.NetworkPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class PokeStaffCmdPacket implements NetworkPacket, CustomPacketPayload {
    protected final int slot;
    private final boolean isFromPokeStaff;

    public PokeStaffCmdPacket(int slot, boolean isFromPokeStaff) {
        this.isFromPokeStaff = isFromPokeStaff;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public boolean isFromPokeStaff() {
        return isFromPokeStaff;
    }
}
