package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.common.collect.ImmutableMap;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class FOFPokemonRangeTask extends Behavior<LivingEntity> {
    public FOFPokemonRangeTask() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT
        ));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity pokemon) {
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            LivingEntity target = FOFPokemonAttackTask.getTarget(pokemonEntity);
            if (target != null && target.isAlive()) {
                return PokemonUtils.shouldShoot(pokemonEntity) && BehaviorUtils.canSee(pokemonEntity, target) && isWithinAttackRange(pokemonEntity, target);
            }
        }
        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity pokemon, long gameTime) {
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            return pokemonEntity.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(level, pokemonEntity);
        }
        return false;
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity pokemon, long gameTime) {
    }

    @Override
    protected void tick(ServerLevel level, LivingEntity pokemon, long gameTime) {
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            LivingEntity target = FOFPokemonAttackTask.getTarget(pokemonEntity);
            if (target != null) {
                pokemonEntity.setTarget(target);
                boolean canSee = pokemonEntity.getSensing().hasLineOfSight(target);
                int attackTime = FOFPokemonAttackTask.getAttackTime(pokemonEntity);
                if (attackTime == 7 && (((PokemonInterface) pokemonEntity).usingSound())) {
                    PokemonUtils.createSonicBoomParticle(pokemonEntity, target);
                }
                if (attackTime % 5 == 0 && (((PokemonInterface) pokemonEntity).usingMagic())) {
                    PokemonAttackEffect.makeMagicAttackParticle(pokemonEntity, target);
                }
                if (attackTime == 0) {
                    if (!canSee) {
                        return;
                    }
                    performRangedAttack(pokemonEntity, target);
                } else if (attackTime < 0) {
                    FOFPokemonAttackTask.refreshAttackTime(pokemonEntity, 10);
                }
            }
        }
    }

    protected boolean isWithinAttackRange(LivingEntity pokemon, LivingEntity target) {
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            double d = pokemonEntity.distanceTo(target);
            return d < PokemonUtils.getAttackRadius();
        }
        return false;
    }

    protected void performRangedAttack(PokemonEntity pokemonEntity, LivingEntity target) {
        double d = pokemonEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        PokemonAttackEffect.pokemonPerformRangedAttack(pokemonEntity, target);
        FOFPokemonAttackTask.resetAttackTime(pokemonEntity, d);
        pokemonEntity.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, FOFPokemonAttackTask.getAttackTime(pokemonEntity));
    }
}
