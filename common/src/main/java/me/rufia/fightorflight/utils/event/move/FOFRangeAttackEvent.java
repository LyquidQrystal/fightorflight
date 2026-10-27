package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.world.entity.LivingEntity;

public abstract class FOFRangeAttackEvent extends FOFMoveEvent {
    public FOFRangeAttackEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration, user, target, move);
    }
}
