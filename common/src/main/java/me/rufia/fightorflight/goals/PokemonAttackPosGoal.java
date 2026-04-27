package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.utils.FOFUtils;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.RayTrace;
import net.minecraft.core.Vec3i;

import java.util.EnumSet;

@Deprecated
public class PokemonAttackPosGoal extends PokemonAttackGoal {
    private final PokemonEntity pokemonEntity;
    private final double speedModifier;

    public PokemonAttackPosGoal(PokemonEntity pokemonEntity, double speedModifier) {
        super(pokemonEntity, speedModifier);
        this.pokemonEntity = pokemonEntity;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return PokemonUtils.attackPositionAvailable(pokemonEntity) && PokemonUtils.shouldShoot(pokemonEntity);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && !this.pokemonEntity.getNavigation().isDone();
    }

    @Override
    public void tick() {
        if (!canSee()) {
            Vec3i vec3i = FOFUtils.stringToVec3i(PokemonUtils.getCommandData(pokemonEntity));
            if (vec3i != null) {
                pokemonEntity.getLookControl().setLookAt(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            }
        }
    }

    protected boolean canSee() {
        Vec3i vec3i = FOFUtils.stringToVec3i(PokemonUtils.getCommandData(pokemonEntity));
        var result = RayTrace.rayTraceBlock(pokemonEntity, PokemonUtils.getAttackRadius());
        var pos = result.getBlockPos();
        return vec3i != null && vec3i.getX() == pos.getX() && vec3i.getY() == pos.getY() && vec3i.getZ() == pos.getZ();//TODO implement it
    }
}
