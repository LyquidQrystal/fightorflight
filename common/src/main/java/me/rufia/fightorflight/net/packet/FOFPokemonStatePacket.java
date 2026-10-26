package me.rufia.fightorflight.net.packet;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.net.NetworkPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Full snapshot of the pokemon state that the client needs for the HUD and the beam renderer.
 * <p>
 * This replaces the SynchedEntityData accessors that used to be injected into
 * {@code PokemonEntity#defineSynchedData} by {@code PokemonEntityMixin}, which conflicted with other mods
 * mixing into the same method.
 * <p>
 * Only the fields that are actually read on the client are transferred. The remaining FOF state
 * (attack mode, cry cooldown, poke staff command, target block pos, captured by) is server side only
 * and stays in plain fields.
 */
public class FOFPokemonStatePacket implements NetworkPacket, CustomPacketPayload {
    public static final ResourceLocation POKEMON_STATE_PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, "pokemon_state");
    public static final Type<FOFPokemonStatePacket> TYPE = new Type<>(POKEMON_STATE_PACKET_ID);
    public static final StreamCodec<FriendlyByteBuf, FOFPokemonStatePacket> STREAM_CODEC;

    private final int entityId;
    private final int attackTargetId;
    private final String moveName;
    private final int attackTime;
    private final int maxAttackTime;
    private final int moveDuration;
    private final int moveDurationOriginal;

    public FOFPokemonStatePacket(int entityId, int attackTargetId, String moveName, int attackTime, int maxAttackTime, int moveDuration, int moveDurationOriginal) {
        this.entityId = entityId;
        this.attackTargetId = attackTargetId;
        this.moveName = moveName == null ? "" : moveName;
        this.attackTime = attackTime;
        this.maxAttackTime = maxAttackTime;
        this.moveDuration = moveDuration;
        this.moveDurationOriginal = moveDurationOriginal;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getAttackTargetId() {
        return attackTargetId;
    }

    public String getMoveName() {
        return moveName;
    }

    public int getAttackTime() {
        return attackTime;
    }

    public int getMaxAttackTime() {
        return maxAttackTime;
    }

    public int getMoveDuration() {
        return moveDuration;
    }

    public int getMoveDurationOriginal() {
        return moveDurationOriginal;
    }

    static {
        // StreamCodec.composite only goes up to six components, so this payload is encoded by hand.
        STREAM_CODEC = StreamCodec.<FriendlyByteBuf, FOFPokemonStatePacket>of(
                (buf, packet) -> {
                    buf.writeVarInt(packet.entityId);
                    buf.writeVarInt(packet.attackTargetId);
                    buf.writeUtf(packet.moveName);
                    buf.writeVarInt(packet.attackTime);
                    buf.writeVarInt(packet.maxAttackTime);
                    buf.writeVarInt(packet.moveDuration);
                    buf.writeVarInt(packet.moveDurationOriginal);
                },
                buf -> new FOFPokemonStatePacket(
                        buf.readVarInt(),
                        buf.readVarInt(),
                        buf.readUtf(),
                        buf.readVarInt(),
                        buf.readVarInt(),
                        buf.readVarInt(),
                        buf.readVarInt()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
