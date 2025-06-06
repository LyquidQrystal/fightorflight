package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.activestate.ActivePokemonState;
import com.cobblemon.mod.common.pokemon.activestate.PokemonState;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.item.PokeStaff;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.NetworkPacketHandler;
import me.rufia.fightorflight.net.packet.SendMoveSlotPacket;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SendMoveSlotHandler implements NetworkPacketHandler<SendMoveSlotPacket> {
    @Override
    public void handle(SendMoveSlotPacket packet, NetworkManager.PacketContext context) {
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
                    if (pokemonEntity != null) {
                        int moveSlot = packet.getMoveSlot();
                        ItemStack stack = getStack(player);
                        if (stack == null) {
                            if (PokemonUtils.shouldCheckPokeStaff()) {
                                return;
                            }
                        } else {
                            PokeStaff staff = (PokeStaff) stack.getItem();
                            if (!packet.isFromPokeStaff()) {
                                staff.setMoveSlot(stack, moveSlot);
                                staff.setCommandMode(stack, PokeStaffComponent.CMDMODE.NOCMD.name());
                                staff.setMode(stack, PokeStaffComponent.MODE.SEND.name());
                            }
                        }
                        if (PokemonAttackEffect.canChangeMove(pokemonEntity)) {
                            Move move = pokemon.getMoveSet().get(moveSlot);
                            if (move != null) {
                                ((PokemonInterface) pokemonEntity).switchMove(move);
                                player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.move", pokemon.getDisplayName(), move.getDisplayName()));
                            }
                        } else {
                            player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.move.failed", pokemon.getDisplayName()));
                        }
                    }
                }
            }
        }
    }

    private ItemStack getStack(Player player) {
        if (player.getMainHandItem().is(ItemFightOrFlight.POKESTAFF.get())) {
            return player.getMainHandItem();
        } else if (player.getOffhandItem().is(ItemFightOrFlight.POKESTAFF.get())) {
            return player.getOffhandItem();
        } else {
            return null;
        }
    }
}
