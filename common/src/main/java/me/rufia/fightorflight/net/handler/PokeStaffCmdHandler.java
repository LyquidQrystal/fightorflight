package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.activestate.ActivePokemonState;
import com.cobblemon.mod.common.pokemon.activestate.PokemonState;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.PokeStaffCmdPacket;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class PokeStaffCmdHandler<T extends PokeStaffCmdPacket> implements NetworkPacketHandler<T> {
    protected ItemStack getStack(Player player) {
        if (player.getMainHandItem().is(ItemFightOrFlight.POKESTAFF.get())) {
            return player.getMainHandItem();
        } else if (player.getOffhandItem().is(ItemFightOrFlight.POKESTAFF.get())) {
            return player.getOffhandItem();
        } else {
            return null;
        }
    }

    protected void handlePacket(T packet, NetworkManager.PacketContext context) {
        Player player = context.getPlayer();
        int slot = packet.getSlot();
        if (player instanceof ServerPlayer serverPlayer) {
            Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).get(slot);
            if (pokemon != null) {
                PokemonState state = pokemon.getState();
                if (state instanceof ShoulderedState || !(state instanceof ActivePokemonState activePokemonState)) {
                    //nothing to do
                } else {
                    PokemonEntity pokemonEntity = activePokemonState.getEntity();
                    processCommand(pokemonEntity, player, packet);
                }
            }
        }
    }

    protected void processCommand(PokemonEntity pokemonEntity, Player player, T packet) {
        if (pokemonEntity != null) {
            ItemStack stack = getStack(player);
            if (stack == null) {
                if (PokemonUtils.shouldCheckPokeStaff()) {
                    return;
                }
            } else {
                editStaff(stack, packet);
            }
            finalProcess(pokemonEntity, player, packet);
        }
    }

    protected abstract void editStaff(ItemStack stack, T packet);

    protected abstract void finalProcess(PokemonEntity pokemonEntity, Player player, T packet);
}
