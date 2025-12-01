package me.rufia.fightorflight.entity.ai.tasks;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.google.common.collect.ImmutableMap;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class FOFPokemonRangeTask extends Behavior<LivingEntity> {
    protected int seeTime;
    private final float attackRadiusSqr;

    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    //private int ticksUntilNextPathFinding = 0;

    private final double speedModifier;

    public FOFPokemonRangeTask() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED,
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT
                //MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT//No, you don't need to care about the cooldown. You need to move during the cooldown.
        ));
        this.seeTime = 0;
        attackRadiusSqr = 64;
        speedModifier = 0.5;
        CobblemonFightOrFlight.LOGGER.info("Range task created");
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, LivingEntity pokemon) {
        CobblemonFightOrFlight.LOGGER.info("[{}] Yeah, we're checking the conditions, sir.", pokemon.getName());
        var targetOpt = pokemon.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (targetOpt.isPresent()) {
            LivingEntity target = targetOpt.get();
            CobblemonFightOrFlight.LOGGER.info("Target Confirmed");
            if (pokemon instanceof PokemonEntity pokemonEntity) {
                //LivingEntity livingEntity = pokemonEntity.getTarget();
                CobblemonFightOrFlight.LOGGER.info("Final checking.");
                return BehaviorUtils.canSee(pokemonEntity, target) && isWithinAttackRange(pokemonEntity, target);
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
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            FOFPokemonAttackTask.resetAttackTime(pokemonEntity, 0);
        }
    }

    @Override
    protected void tick(ServerLevel level, LivingEntity pokemon, long gameTime) {
        CobblemonFightOrFlight.LOGGER.info("Yeah, we're ticking, sir.");
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            LivingEntity target = pokemonEntity.getTarget();
            if (target != null) {
                double d = pokemonEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
                boolean canSee = pokemonEntity.getSensing().hasLineOfSight(target);
                if (canSee) {
                    ++seeTime;
                } else {
                    seeTime = 0;
                    FOFPokemonAttackTask.resetAttackTime(pokemonEntity, d);
                }
                rangeAttackPathFinding(pokemonEntity, target, canSee, d);
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
                    FOFPokemonAttackTask.resetAttackTime(pokemonEntity, d);
                    performRangedAttack(pokemonEntity, target);
                } else if (attackTime < 0) {
                    FOFPokemonAttackTask.resetAttackTime(pokemonEntity, d);
                }
            }
        }
    }

    private void rangeAttackPathFinding(PokemonEntity pokemonEntity, LivingEntity target, boolean canSee, double distance) {
        if (!(distance > (double) attackRadiusSqr) && seeTime >= 5 && canSee) {
            pokemonEntity.getNavigation().stop();
            ++strafingTime;
        } else {
            pokemonEntity.getNavigation().moveTo(target, speedModifier);
            strafingTime = -1;
        }
        if (strafingTime >= 10) {
            if ((double) pokemonEntity.getRandom().nextFloat() < 0.3) {
                strafingClockwise = !strafingClockwise;
            }
            if ((double) pokemonEntity.getRandom().nextFloat() < 0.3) {
                strafingBackwards = !strafingBackwards;
            }
            strafingTime = 0;
        }
        if (strafingTime > -1) {
            if (distance > (double) (attackRadiusSqr * 0.8F)) {
                strafingBackwards = false;
            } else if (distance < (double) (attackRadiusSqr * 0.2F)) {
                strafingBackwards = true;
            }
            pokemonEntity.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            Entity vehicle = pokemonEntity.getControlledVehicle();
            if (vehicle instanceof Mob mob) {
                mob.lookAt(pokemonEntity, 30.0F, 30.0F);
            }
        }
        pokemonEntity.getLookControl().setLookAt(target);
    }

    protected boolean isWithinAttackRange(LivingEntity pokemon, LivingEntity target) {
        if (pokemon instanceof PokemonEntity pokemonEntity) {
            double d = pokemonEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
            return d < 16;
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
