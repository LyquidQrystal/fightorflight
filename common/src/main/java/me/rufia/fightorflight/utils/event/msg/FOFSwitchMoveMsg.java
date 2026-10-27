package me.rufia.fightorflight.utils.event.msg;

import me.rufia.fightorflight.net.packet.MoveInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class FOFSwitchMoveMsg extends FOFMsg {
    private int pokemonId;
    private String moveName;
    private int faction;


    public FOFSwitchMoveMsg(int pokemonId, String moveName, int faction, int createTime) {
        super(100, createTime);
        this.pokemonId = pokemonId;
        this.moveName = moveName;
        this.faction = faction;
    }

    public FOFSwitchMoveMsg(MoveInfoPacket packet, int createTime) {
        super(100, createTime);
        this.pokemonId = packet.getPokemonId();
        this.moveName = packet.getMoveName();
        this.faction = packet.getFaction();
    }

    public int getFaction() {
        return faction;
    }

    public MutableComponent getMessageText() {
        var move = Component.translatable("cobblemon.move.%s".formatted(moveName));
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var entity = level.getEntity(pokemonId);
            if (entity != null) {
                return Component.translatable("client.fightorflight.hud.change_move", entity.getDisplayName(), move);
            }
        }
        return null;
    }
}
