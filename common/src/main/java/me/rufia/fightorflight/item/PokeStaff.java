package me.rufia.fightorflight.item;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.component.ItemComponentFOF;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.packet.SendCommandPacket;
import me.rufia.fightorflight.net.packet.SendMoveSlotPacket;
import me.rufia.fightorflight.utils.FOFUtils;
import me.rufia.fightorflight.utils.RayTrace;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Objects;

public class PokeStaff extends Item {
    public PokeStaff(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        String mode = getMode(stack);
        if (!mode.isEmpty()) {
            int d = getMoveSlot(stack);
            String cmdMode = getCommandMode(stack);
            Component component;
            //CobblemonFightOrFlight.LOGGER.info(mode);
            switch (PokeStaffComponent.MODE.valueOf(mode)) {
                case SEND -> component = Component.translatable("item.fightorflight.pokestaff.mode.send");
                case SETMOVE -> component = Component.translatable("item.fightorflight.pokestaff.mode.selectmoveslot");
                case SETCMDMODE ->
                        component = Component.translatable("item.fightorflight.pokestaff.mode.selectcommand");
                default -> component = Component.literal("");
            }

            tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc1").append(component));
            tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc2", d + 1));
            tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc3", getTranslatedCmdModeName(cmdMode).getString()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (player.isSecondaryUseActive()) {
            setMode(stack, PokeStaffComponent.getNextMode(getMode(stack)));
            switch (PokeStaffComponent.MODE.valueOf(getMode(stack))) {
                case SETMOVE -> {
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.selectmoveslot"));
                    }
                }
                case SEND -> {
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.send"));
                    }
                }
                case SETCMDMODE -> {
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.selectcommand"));
                    }
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        String mode = getMode(stack);
        if (mode.equals(PokeStaffComponent.MODE.SETMOVE.name())) {
            //CobblemonFightOrFlight.LOGGER.info("SELECTING MOVES");
            int nextMoveSlot = getMoveSlot(stack) + 1;
            setMoveSlot(stack, nextMoveSlot);
            setCommandMode(stack, PokeStaffComponent.CMDMODE.NOCMD.name());
            if (player.level().isClientSide) {
                player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.desc2", nextMoveSlot % 4 + 1));
            }
        }
        if (mode.equals(PokeStaffComponent.MODE.SETCMDMODE.name())) {
            commandModeSelectNext(stack, getCommandMode(stack));
            setMoveSlot(stack, -1);
            if (player.level().isClientSide) {
                player.sendSystemMessage(getTranslatedCmdModeName(getCommandMode(stack)));
            }
        }

        if (mode.equals(PokeStaffComponent.MODE.SEND.name())) {
            //CobblemonFightOrFlight.LOGGER.info("SENDING COMMAND");
            PokeStaffComponent.CMDMODE cmdmode = PokeStaffComponent.CMDMODE.valueOf(getCommandMode(stack));
            String cmdData = FOFUtils.createCommandData(player, cmdmode);

            if (player.level().isClientSide) {
                int slot = CobblemonClient.INSTANCE.getStorage().getSelectedSlot();
                if (!Objects.equals(getCommandMode(stack), PokeStaffComponent.CMDMODE.NOCMD.name())) {
                    NetworkManager.sendToServer(new SendCommandPacket(slot, getCommandMode(stack), cmdData, true));
                } else {
                    NetworkManager.sendToServer(new SendMoveSlotPacket(slot, getMoveSlot(stack), true));
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    protected String getMode(ItemStack itemStack) {
        if (itemStack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = itemStack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                return component.mode();//if it has the component it shouldn't be null, I'm not sure.
            }
        }
        return "";
    }

    public int getMoveSlot(ItemStack itemStack) {
        if (itemStack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = itemStack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                return component.moveSlot();//if it has the component it shouldn't be null, I'm not sure.
            }
        }
        return -1;
    }

    public String getCommandMode(ItemStack itemStack) {
        if (itemStack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = itemStack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                return component.cmdmode();//if it has the component it shouldn't be null, I'm not sure.
            }
        }
        return PokeStaffComponent.CMDMODE.NOCMD.name();
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    public void setMoveSlot(ItemStack stack, int moveSlot) {
        if (stack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = stack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                component.setMoveSlot(moveSlot % 4, stack);//if it has the component it shouldn't be null, I'm not sure.
            }
        }
    }

    public void setCommandMode(ItemStack stack, String mode) {
        if (stack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = stack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                component.setCmdmode(mode, stack);//if it has the component it shouldn't be null, I'm not sure.
            }
        }
    }

    public void setMode(ItemStack stack, String mode) {
        if (stack.has(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT)) {
            PokeStaffComponent component = stack.get(ItemComponentFOF.POKE_STAFF_COMMAND_MODE_COMPONENT);
            if (component != null) {
                component.setMode(mode, stack);//if it has the component it shouldn't be null, I'm not sure.
            }
        }
    }

    protected void commandModeSelectNext(ItemStack stack, String mode) {
        String cmd;
        switch (PokeStaffComponent.CMDMODE.valueOf(mode)) {
            case MOVE_ATTACK -> cmd = PokeStaffComponent.CMDMODE.MOVE.name();
            case MOVE -> cmd = PokeStaffComponent.CMDMODE.STAY.name();
            case STAY -> cmd = PokeStaffComponent.CMDMODE.ATTACK.name();
            case ATTACK -> cmd = PokeStaffComponent.CMDMODE.ATTACK_POSITION.name();
            case ATTACK_POSITION -> cmd = PokeStaffComponent.CMDMODE.NOCMD.name();
            case NOCMD -> cmd = PokeStaffComponent.CMDMODE.CLEAR.name();
            case CLEAR -> cmd = PokeStaffComponent.CMDMODE.MOVE_ATTACK.name();
            default -> cmd = PokeStaffComponent.CMDMODE.NOCMD.name();
        }
        setCommandMode(stack, cmd);
    }

    public static Component getTranslatedCmdModeName(String cmdModeName) {
        Component component;
        switch (PokeStaffComponent.CMDMODE.valueOf(cmdModeName)) {
            case MOVE_ATTACK -> component = Component.translatable("item.fightorflight.pokestaff.command.move_attack");
            case MOVE -> component = Component.translatable("item.fightorflight.pokestaff.command.move");
            case STAY -> component = Component.translatable("item.fightorflight.pokestaff.command.stay");
            case ATTACK -> component = Component.translatable("item.fightorflight.pokestaff.command.attack_target");
            case ATTACK_POSITION ->
                    component = Component.translatable("item.fightorflight.pokestaff.command.attack_position");
            case CLEAR -> component = Component.translatable("item.fightorflight.pokestaff.command.clear_cmd");
            default -> component = Component.translatable("item.fightorflight.pokestaff.command.no_cmd");
        }
        return component;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
