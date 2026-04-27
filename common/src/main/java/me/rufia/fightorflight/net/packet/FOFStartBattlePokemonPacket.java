package me.rufia.fightorflight.net.packet;

import io.netty.buffer.ByteBuf;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.net.NetworkPacket;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class FOFStartBattlePokemonPacket implements NetworkPacket, CustomPacketPayload {
    public static final ResourceLocation START_BATTLE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "start_battle");
    public static final Type<FOFStartBattlePokemonPacket> TYPE = new Type<>(START_BATTLE_PACKET_ID);
    public static StreamCodec<ByteBuf, FOFStartBattlePokemonPacket> STREAM_CODEC;
    protected int pokemonEntityID;
    protected UUID playerPokemonUUID;

    public int getPokemonEntityID() {
        return pokemonEntityID;
    }

    public UUID getPlayerPokemonUUID() {
        return playerPokemonUUID;
    }

    public FOFStartBattlePokemonPacket(int pokemonEntityID, UUID playerPokemonUUID) {
        this.pokemonEntityID = pokemonEntityID;
        this.playerPokemonUUID = playerPokemonUUID;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, FOFStartBattlePokemonPacket::getPokemonEntityID,
                UUIDUtil.STREAM_CODEC, FOFStartBattlePokemonPacket::getPlayerPokemonUUID,
                FOFStartBattlePokemonPacket::new
        );
    }
}
