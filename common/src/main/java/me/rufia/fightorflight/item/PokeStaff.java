package me.rufia.fightorflight.item;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.RayTrace;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PokeStaff extends Item {
    enum MODE {
        SEND, SETMOVE, SETCMDMODE
    }

    public enum CMDMODE {
        MOVE_ATTACK, MOVE, STAY, ATTACK, ATTACK_POSITION, NOCMD, CLEAR
    }

    public PokeStaff(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();

        if (tag.contains("command")) {
            String modeTag = tag.getCompound("command").getString("mode");
            if (!modeTag.isEmpty()) {
                int d = getMoveSlot(stack);
                String cmdMode = getCommandMode(stack);
                Component component;
                switch (MODE.valueOf(modeTag)) {
                    case SEND -> component = Component.translatable("item.fightorflight.pokestaff.mode.send");
                    case SETMOVE ->
                            component = Component.translatable("item.fightorflight.pokestaff.mode.selectmoveslot");
                    case SETCMDMODE ->
                            component = Component.translatable("item.fightorflight.pokestaff.mode.selectcommand");
                    default -> component = Component.literal("");
                }

                tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc1").append(component));
                tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc2", d + 1));
                tooltipComponents.add(Component.translatable("item.fightorflight.pokestaff.desc3", getTranslatedCmdModeName(cmdMode).getString()));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        CompoundTag tag = stack.getOrCreateTag();
        initTag(stack);

        if (player.isSecondaryUseActive()) {
            //CobblemonFightOrFlight.LOGGER.info("SNEAKING");
            CompoundTag tag2 = tag.getCompound("command");
            switch (MODE.valueOf(tag2.getString("mode"))) {
                case SETMOVE -> {
                    tag2.putString("mode", MODE.SETCMDMODE.name());
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.selectcommand"));
                    }
                    //CobblemonFightOrFlight.LOGGER.info("sending");
                }
                case SEND -> {
                    tag2.putString("mode", MODE.SETMOVE.name());
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.selectmoveslot"));
                    }
                    //CobblemonFightOrFlight.LOGGER.info("SETTING Moves");
                }
                case SETCMDMODE -> {
                    tag2.putString("mode", MODE.SEND.name());
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.mode.send"));
                    }
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        String mode = getMode(stack);
        if (MODE.valueOf(tag.getCompound("command").getString("mode")) == MODE.SETMOVE) {
            //CobblemonFightOrFlight.LOGGER.info("SELECTING MOVES");
            int nextMoveSlot = getMoveSlot(stack) + 1;
            setMoveSlot(stack, nextMoveSlot, player);
            setCommandMode(stack, CMDMODE.NOCMD.name());
            if (player.level().isClientSide) {
                player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.desc2", nextMoveSlot % 4 + 1));
            }
        }
        if (MODE.valueOf(tag.getCompound("command").getString("mode")) == MODE.SETCMDMODE) {
            commandModeSelectNext(stack, getCommandMode(stack));
            setMoveSlot(stack, -1, player);
            if (player.level().isClientSide) {
                player.sendSystemMessage(getTranslatedCmdModeName(getCommandMode(stack)));
            }
        }

        if (mode.equals(MODE.SEND.name())) {
            //CobblemonFightOrFlight.LOGGER.info("SENDING COMMAND");
            CMDMODE cmdmode = CMDMODE.valueOf(getCommandMode(stack));
            String cmdData = "";

            switch (cmdmode) {
                case MOVE, ATTACK_POSITION, MOVE_ATTACK, STAY -> {
                    BlockHitResult result = RayTrace.rayTraceBlock(player, 16);
                    BlockPos blockPos = result.getBlockPos();
                    //CobblemonFightOrFlight.LOGGER.info("VEC3_%s_%s_%s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    cmdData = "VEC3_%s_%s_%s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }
                case ATTACK -> {
                    LivingEntity livingEntity = RayTrace.rayTraceEntity(player, 16);
                    if (livingEntity != null) {
                        cmdData = "ENTITY_%s".formatted(livingEntity.getStringUUID());

                        //CobblemonFightOrFlight.LOGGER.info("ENTITY_%s".formatted(livingEntity.getStringUUID()));
                    }
                }
                default -> cmdData = "";
            }
            if (!Objects.equals(getCommandMode(stack), CMDMODE.NOCMD.name())) {
                for (PokemonEntity pokemonEntity : player.level().getEntitiesOfClass(PokemonEntity.class, AABB.ofSize(player.position(), 8, 8, 8), (pokemonEntity -> Objects.equals(pokemonEntity.getOwner(), player)))) {
                    ((PokemonInterface) (Object) pokemonEntity).setCommand(getCommandMode(stack));
                    ((PokemonInterface) (Object) pokemonEntity).setCommandData(cmdData);
                    if (player.level().isClientSide) {
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.recv.command", pokemonEntity.getName().getString(), cmdmode.name()));
                    }
                }
            }

        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        initTag(stack);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public boolean canSend(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!(tag.contains("command") && stack.is(ItemFightOrFlight.POKESTAFF.get()))) {
            return false;
        }
        return Objects.equals(tag.getCompound("command").getString("mode"), MODE.SEND.name());
    }

    public void sendMoveSlot(Player player, LivingEntity livingEntity, ItemStack itemStack) {
        if (!livingEntity.level().isClientSide && livingEntity instanceof PokemonEntity pokemonEntity) {
            if (pokemonEntity.getOwner() == player) {
                ItemStack heldItem = pokemonEntity.getPokemon().heldItem();
                if (heldItem.is(CobblemonItems.CHOICE_SCARF) || heldItem.is(CobblemonItems.CHOICE_BAND) || heldItem.is(CobblemonItems.CHOICE_SPECS)) {
                    return;
                }
                CompoundTag tag = itemStack.getOrCreateTag();
                if (tag.contains("command") && itemStack.is(ItemFightOrFlight.POKESTAFF.get())) {
                    int moveSlot = getMoveSlot(itemStack);
                    String cmdMode = getCommandMode(itemStack);
                    if (moveSlot != -1) {
                        Move move = pokemonEntity.getPokemon().getMoveSet().get(moveSlot);
                        if (move == null) {
                            move = pokemonEntity.getPokemon().getMoveSet().get(0);
                        }
                        ((PokemonInterface) (Object) pokemonEntity).setCurrentMove(move);
                        player.sendSystemMessage(Component.translatable("item.fightorflight.pokestaff.send.result.move").append(move.getDisplayName()));
                        //CobblemonFightOrFlight.LOGGER.info(moveSlot + pokemonEntity.getPokemon().getMoveSet().get(moveSlot).getName());
                    }

                    ((PokemonInterface) (Object) pokemonEntity).setCommand(cmdMode);
                }
            }
        }
    }

    private void initTag(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (!tag.contains("command")) {
            CompoundTag tag2 = itemStack.getOrCreateTagElement("command");
            tag2.putString("mode", MODE.SETMOVE.name());
            tag2.putInt("move_slot", 0);
            tag2.putString("command_mode", CMDMODE.NOCMD.name());
            tag.put("commnad", tag2);
        }
    }

    protected String getMode(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (itemStack.is(ItemFightOrFlight.POKESTAFF.get())) {
            if (tag.contains("command")) {
                return tag.getCompound("command").getString("mode");
            }
        }
        return "";
    }

    public int getMoveSlot(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (itemStack.is(ItemFightOrFlight.POKESTAFF.get())) {
            if (tag.contains("command")) {
                return tag.getCompound("command").getInt("move_slot");
            }
        }
        return -1;
    }

    public String getCommandMode(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (itemStack.is(ItemFightOrFlight.POKESTAFF.get())) {
            if (tag.contains("command")) {
                return tag.getCompound("command").getString("command_mode");
            }
        }
        return CMDMODE.NOCMD.name();
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    protected void setMoveSlot(ItemStack stack, int moveSlot, Player player) {
        stack.getOrCreateTag().getCompound("command").putInt("move_slot", moveSlot % 4);
    }

    protected void setCommandMode(ItemStack stack, String mode) {
        stack.getOrCreateTag().getCompound("command").putString("command_mode", mode);
    }

    protected void commandModeSelectNext(ItemStack stack, String mode) {
        String cmd;
        switch (CMDMODE.valueOf(mode)) {
            case MOVE_ATTACK -> cmd = CMDMODE.MOVE.name();
            case MOVE -> cmd = CMDMODE.STAY.name();
            case STAY -> cmd = CMDMODE.ATTACK.name();
            case ATTACK -> cmd = CMDMODE.ATTACK_POSITION.name();
            case ATTACK_POSITION -> cmd = CMDMODE.NOCMD.name();
            case NOCMD -> cmd = CMDMODE.CLEAR.name();
            case CLEAR -> cmd = CMDMODE.MOVE_ATTACK.name();
            default -> cmd = CMDMODE.NOCMD.name();
        }
        setCommandMode(stack, cmd);
    }

    protected Component getTranslatedCmdModeName(String cmdModeName) {
        Component component;
        switch (CMDMODE.valueOf(cmdModeName)) {
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
