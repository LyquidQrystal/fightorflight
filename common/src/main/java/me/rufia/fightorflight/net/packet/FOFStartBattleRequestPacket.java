package me.rufia.fightorflight.net.packet;

import io.netty.buffer.ByteBuf;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.net.NetworkPacket;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

//To start battle with the selected Pokemon, we need to get the selected slot. However, it's only available on the client side, so the server side need to send a packet to let the client side send a packet back to the server side start the battle.
public class FOFStartBattleRequestPacket implements NetworkPacket, CustomPacketPayload {
    public static final ResourceLocation START_BATTLE_REQUEST_PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "start_battle_request");
    public static final Type<FOFStartBattleRequestPacket> TYPE = new Type<>(START_BATTLE_REQUEST_PACKET_ID);
    public static StreamCodec<ByteBuf, FOFStartBattleRequestPacket> STREAM_CODEC;
    protected int pokemonEntityID;

    public int getPokemonEntityID() {
        return pokemonEntityID;
    }

    public FOFStartBattleRequestPacket(int pokemonEntityID) {
        this.pokemonEntityID = pokemonEntityID;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, FOFStartBattleRequestPacket::getPokemonEntityID,
                FOFStartBattleRequestPacket::new
        );
    }
}
