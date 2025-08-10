package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.item.PokeStaff;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.packet.SendMoveSlotPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class SendMoveSlotHandler extends PokeStaffCmdHandler<SendMoveSlotPacket> {
    private int moveSlot;

    @Override
    public void handle(SendMoveSlotPacket packet, NetworkManager.PacketContext context) {
        moveSlot = packet.getMoveSlot();
        handlePacket(packet, context);
    }

    @Override
    protected void editStaff(ItemStack stack, SendMoveSlotPacket packet) {
        PokeStaff staff = (PokeStaff) stack.getItem();
        if (!packet.isFromPokeStaff()) {
            staff.setMoveSlot(stack, moveSlot);
            staff.setCommandMode(stack, PokeStaffComponent.CMDMODE.NOCMD.name());
            staff.setMode(stack, PokeStaffComponent.MODE.SEND.name());
        }
    }

    @Override
    protected void finalProcess(PokemonEntity pokemonEntity, Player player, SendMoveSlotPacket packet) {
        Move move = pokemonEntity.getPokemon().getMoveSet().get(moveSlot);
        String oldMoveName = ((PokemonInterface) pokemonEntity).getCurrentMove();
        if (move != null) {
            if (Objects.equals(move.getName(), oldMoveName)) {
                ((PokemonInterface) pokemonEntity).tryUsingStatusMoves();
            } else {
                if (PokemonAttackEffect.canChangeMove(pokemonEntity)) {
                    ((PokemonInterface) pokemonEntity).switchMove(move);
                    player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.move", pokemonEntity.getPokemon().getDisplayName(), move.getDisplayName()));
                } else {
                    player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.move.failed", pokemonEntity.getPokemon().getDisplayName()));
                }
            }
        }
    }
}
