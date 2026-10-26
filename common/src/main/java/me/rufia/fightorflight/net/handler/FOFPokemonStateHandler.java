package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.FOFPokemonStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Client side receiver of {@link FOFPokemonStatePacket}.
 * <p>
 * Nothing is queued on the client: the values are simply written into the local fields of the entity through
 * {@link PokemonInterface}. If the entity is not known yet the snapshot is dropped, the server re-sends a full
 * snapshot after {@code FOFStateSync#RESYNC_INTERVAL} ticks.
 */
public class FOFPokemonStateHandler implements NetworkPacketHandler<FOFPokemonStatePacket> {
    @Override
    public void handle(FOFPokemonStatePacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> apply(packet));
    }

    private static void apply(FOFPokemonStatePacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(packet.getEntityId());
        if (!(entity instanceof PokemonEntity)) {
            return;
        }
        if (!(entity instanceof PokemonInterface state)) {
            return;
        }
        state.setAttackTargetId(packet.getAttackTargetId());
        state.setCurrentMove(packet.getMoveName());
        state.setAttackTime(packet.getAttackTime());
        state.setMaxAttackTime(packet.getMaxAttackTime());
        state.setMoveDuration(packet.getMoveDuration());
        state.setMoveDurationOriginal(packet.getMoveDurationOriginal());
    }
}
