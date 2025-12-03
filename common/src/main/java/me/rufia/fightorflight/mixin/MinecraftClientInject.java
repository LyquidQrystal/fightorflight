package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.CobblemonNetwork;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.net.messages.server.BattleChallengePacket;
import com.cobblemon.mod.common.net.messages.server.RequestPlayerInteractionsPacket;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.architectury.networking.NetworkManager;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.client.keybinds.CommandKeybind;
import me.rufia.fightorflight.client.keybinds.KeybindFightOrFlight;
import me.rufia.fightorflight.item.ItemFightOrFlight;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.net.packet.SendCommandPacket;
import me.rufia.fightorflight.net.packet.SendMoveSlotPacket;
import me.rufia.fightorflight.utils.FOFUtils;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientInject {
    @Shadow
    public static Minecraft getInstance() {
        return null;
    }

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(method = "handleKeybinds", at = @At("TAIL"))
    private void postTick(CallbackInfo ci) {
        if (KeybindFightOrFlight.START_BATTLE.consumeClick()) {
            startBattle();
        }
        if (KeybindFightOrFlight.MOVE_SLOT_1.consumeClick()) {
            sendMoveSlotPacket(KeybindFightOrFlight.MOVE_SLOT_1.getMoveSlot());
        } else if (KeybindFightOrFlight.MOVE_SLOT_2.consumeClick()) {
            sendMoveSlotPacket(KeybindFightOrFlight.MOVE_SLOT_2.getMoveSlot());
        } else if (KeybindFightOrFlight.MOVE_SLOT_3.consumeClick()) {
            sendMoveSlotPacket(KeybindFightOrFlight.MOVE_SLOT_3.getMoveSlot());
        } else if (KeybindFightOrFlight.MOVE_SLOT_4.consumeClick()) {
            sendMoveSlotPacket(KeybindFightOrFlight.MOVE_SLOT_4.getMoveSlot());
        }
        for (CommandKeybind keybind : KeybindFightOrFlight.commandKeybinds) {
            if (keybind.consumeClick()) {
                sendCommandModePacket(keybind.getCmdmode());
                break;
            }
        }
    }

    private void sendMoveSlotPacket(int moveSlot) {
        if (getInstance() == null) {
            return;
        }
        var player = getInstance().player;
        if (player != null) {
            if (PokemonUtils.shouldCheckPokeStaff()) {
                var stack = player.getMainHandItem();
                if (!stack.is(ItemFightOrFlight.POKESTAFF.get())) {
                    if (!player.getOffhandItem().is(ItemFightOrFlight.POKESTAFF.get())) {
                        return;
                    }
                }
            }
            int slot = CobblemonClient.INSTANCE.getStorage().getSelectedSlot();
            NetworkManager.sendToServer(new SendMoveSlotPacket(slot, moveSlot));
        }
    }

    private void sendCommandModePacket(PokeStaffComponent.CMDMODE cmdmode) {
        var player = getInstance().player;
        if (player == null) {
            return;
        }
        String cmdData = FOFUtils.createCommandData(player, cmdmode);
        int slot = CobblemonClient.INSTANCE.getStorage().getSelectedSlot();
        NetworkManager.sendToServer(new SendCommandPacket(slot, cmdmode.name(), cmdData));
    }

    private void startBattle() {
        var player = getInstance().player;
        boolean isSpectator = player.isSpectator();
        boolean playerIsNotAvailable = CobblemonClient.INSTANCE.getBattle() != null;
        boolean otherConditions = !(CobblemonClient.INSTANCE.getStorage().getSelectedSlot() != -1 && getInstance().screen == null);
        if (isSpectator || playerIsNotAvailable || otherConditions) {
            return;
        }

        Pokemon pokemon = CobblemonClient.INSTANCE.getStorage().getParty().get(CobblemonClient.INSTANCE.getStorage().getSelectedSlot());
        if (pokemon != null && pokemon.getCurrentHealth() > 0) {
            var entities = player.clientLevel.getEntitiesOfClass(PokemonEntity.class, AABB.ofSize(player.getPosition(player.tickCount), 16, 16, 16),
                    (pokemonEntity) -> pokemonEntity.getTarget() == player
            );
            for (PokemonEntity pokemonEntity : entities) {
                if (pokemonEntity.getOwner() == null && pokemonEntity.canBattle(player)) {
                    BattleChallengePacket packet = new BattleChallengePacket(pokemonEntity.getId(), pokemon.getUuid(), BattleFormat.Companion.getGEN_9_SINGLES());
                    //packet.sendToServer();
                    CobblemonNetwork.sendToServer(packet);
                    //CobblemonFightOrFlight.LOGGER.info("sending battle packet");
                    //player.sendSystemMessage(Component.literal("Sending battle packet - message"));
                    break;
                } else if (pokemonEntity.getOwner() != player) {
                    if (pokemonEntity.getOwner() instanceof Player) {
                        CobblemonNetwork.sendToServer(new RequestPlayerInteractionsPacket(pokemonEntity.getUUID(), pokemonEntity.getId(), pokemon.getUuid()));
                        //break;
                    }
                } else {
                    //CobblemonFightOrFlight.LOGGER.info("You must be joking");
                }
            }
        }
    }
}
