package me.rufia.fightorflight.net;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.net.packet.FOFPokemonStatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Server to client push of {@link FOFPokemonStatePacket}.
 * <p>
 * The mixin keeps track of which players already received the state and when; this class only knows how
 * to find the receivers and how to build/send the packet.
 */
public final class FOFStateSync {
    /**
     * Matches the vanilla tracking range of a mob (8 chunks). Players outside of it do not receive updates.
     */
    public static final double TRACKING_RANGE = 128.0;
    /**
     * If a player has not received any state for this pokemon for that many ticks, a full snapshot is sent again.
     * This covers players that start tracking the entity after it was spawned, dimension changes and lost packets.
     */
    public static final int RESYNC_INTERVAL = 100;

    private FOFStateSync() {
    }

    /**
     * Players that are close enough to see the entity. Only valid on the logical server.
     */
    public static List<ServerPlayer> nearPlayers(PokemonEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return List.of();
        }
        double rangeSqr = TRACKING_RANGE * TRACKING_RANGE;
        return serverLevel.getPlayers(player -> player.distanceToSqr(entity) <= rangeSqr);
    }

    public static FOFPokemonStatePacket buildPacket(PokemonEntity entity) {
        PokemonInterface state = (PokemonInterface) entity;
        return new FOFPokemonStatePacket(
                entity.getId(),
                state.getAttackTargetId(),
                state.getCurrentMove(),
                state.getAttackTime(),
                state.getMaxAttackTime(),
                state.getMoveDuration(),
                state.getMoveDurationOriginal());
    }

    public static void sendState(PokemonEntity entity, ServerPlayer player) {
        NetworkManager.sendToPlayer(player, buildPacket(entity));
    }

    public static void sendState(PokemonEntity entity, Iterable<ServerPlayer> players) {
        NetworkManager.sendToPlayers(players, buildPacket(entity));
    }
}
