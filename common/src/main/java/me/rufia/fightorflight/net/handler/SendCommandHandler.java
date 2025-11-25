package me.rufia.fightorflight.net.handler;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.PokeStaff;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.packet.SendCommandPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SendCommandHandler extends PokeStaffCmdHandler<SendCommandPacket> {
    private String cmdData;
    private String cmdMode;

    @Override
    public void handle(SendCommandPacket packet, NetworkManager.PacketContext context) {
        cmdData = packet.getCommandData();
        cmdMode = packet.getCommand();
        handlePacket(packet, context);
    }

    @Override
    protected void editStaff(ItemStack stack, SendCommandPacket packet) {
        PokeStaff staff = (PokeStaff) stack.getItem();
        if (!packet.isFromPokeStaff()) {
            staff.setMoveSlot(stack, -1);
            staff.setCommandMode(stack, cmdMode);
            staff.setMode(stack, PokeStaffComponent.MODE.SEND.name());
        }
    }

    @Override
    protected void finalProcess(PokemonEntity pokemonEntity, Player player, SendCommandPacket packet) {
        ((PokemonInterface) pokemonEntity).setCommand(cmdMode);
        ((PokemonInterface) pokemonEntity).setCommandData(cmdData);
        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.command", pokemonEntity.getPokemon().getDisplayName(false), PokeStaff.getTranslatedCmdModeName(cmdMode)));
    }
}
