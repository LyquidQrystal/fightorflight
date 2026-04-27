package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

public class PokemonGoToPosGoal extends Goal {
    private final PokemonEntity pokemonEntity;
    private final double speedModifier;
    private boolean stuck;

    public PokemonGoToPosGoal(PokemonEntity entity, double speedModifier) {
        pokemonEntity = entity;
        this.speedModifier = speedModifier;

    }

    protected boolean moveCommand() {
        return PokemonUtils.moveCommandAvailable(pokemonEntity)
                || PokemonUtils.moveAttackCommandAvailable(pokemonEntity);
    }

    protected boolean stayCommand() {
        return PokemonUtils.stayCommandAvailable(pokemonEntity);
    }

    @Override
    public boolean canUse() {
        return (moveCommand() && !isCloseEnough()) || stayCommand();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    public void start() {
        stuck = false;
    }

    public void tick() {
        if (moveCommand()) {
            approach();
        } else if (stayCommand()) {
            if (!isCloseEnough()) {
                approach();
            }
        }
    }

    public void stop() {
        PokemonUtils.finishMoving(pokemonEntity);
    }

    protected void approach() {
        PokemonUtils.pokemonEntityApproachPos(pokemonEntity, getBlockPos(), speedModifier);
    }

    protected BlockPos getBlockPos() {
        return ((PokemonInterface) pokemonEntity).getTargetBlockPos();
    }

    protected boolean isCloseEnough() {
        return getBlockPos().closerToCenterThan(pokemonEntity.position(), 2);
    }
}
