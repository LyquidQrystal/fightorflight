package me.rufia.fightorflight.net.packet;

import io.netty.buffer.ByteBuf;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.net.NetworkPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class MoveInfoPacket implements NetworkPacket, CustomPacketPayload {
    public static final ResourceLocation MOVE_INFO_PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "send_move_info");
    public static final StreamCodec<ByteBuf, MoveInfoPacket> STREAM_CODEC;
    public static final Type<MoveInfoPacket> TYPE = new Type<>(MOVE_INFO_PACKET_ID);
    private String pokemonName;
    private String moveName;
    private Integer faction;

    public MoveInfoPacket(String pokemonName, String moveName, Integer faction) {
        this.pokemonName = pokemonName;
        this.moveName = moveName;
        this.faction = faction;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getMoveName() {
        return moveName;
    }

    public Integer getFaction() {
        return faction;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, MoveInfoPacket::getPokemonName,
                ByteBufCodecs.STRING_UTF8, MoveInfoPacket::getMoveName,
                ByteBufCodecs.INT, MoveInfoPacket::getFaction,
                MoveInfoPacket::new);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
