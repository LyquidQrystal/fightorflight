package me.rufia.fightorflight.utils.event.move;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.event.FOFAbstractEvent;
import net.minecraft.world.entity.LivingEntity;

public abstract class FOFMoveEvent extends FOFAbstractEvent {
    private final PokemonEntity user;
    private final LivingEntity target;
    private final Move move;

    public FOFMoveEvent(int createTime, int duration, PokemonEntity user, LivingEntity target, Move move) {
        super(createTime, duration);
        this.user = user;
        this.target = target;
        this.move = move;
    }

    public PokemonEntity getUser() {
        return user;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public Move getMove() {
        return move;
    }

    public abstract void use();
}
