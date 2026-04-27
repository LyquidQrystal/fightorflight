package me.rufia.fightorflight.net.packet;

import io.netty.buffer.ByteBuf;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class SendMoveSlotPacket extends PokeStaffCmdPacket {
    public static final ResourceLocation SEND_MOVE_SLOT_PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "send_move_slot");
    protected int moveSlot;
    public static final StreamCodec<ByteBuf, SendMoveSlotPacket> STREAM_CODEC;
    public static final Type<SendMoveSlotPacket> TYPE = new Type<>(SEND_MOVE_SLOT_PACKET_ID);

    public int getMoveSlot() {
        return moveSlot;
    }

    public SendMoveSlotPacket(int slot, int moveSlot, boolean isFromPokeStaff) {
        super(slot, isFromPokeStaff);
        this.moveSlot = moveSlot;
    }

    public SendMoveSlotPacket(int slot, int moveSlot) {
        super(slot, false);
        this.moveSlot = moveSlot;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, SendMoveSlotPacket::getSlot,
                ByteBufCodecs.INT, SendMoveSlotPacket::getMoveSlot,
                SendMoveSlotPacket::new);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
