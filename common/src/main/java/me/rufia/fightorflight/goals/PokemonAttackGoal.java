package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Arrays;
import java.util.EnumSet;

public class PokemonAttackGoal extends Goal {
    private int ticksUntilNewAngerParticle = 0;
    private int ticksUntilNewAngerCry = 0;

    private int seeTime;
    private float attackRadiusSqr;

    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    private int ticksUntilNextPathFinding = 0;

    private final double speedModifier;

    protected PokemonEntity pokemonEntity;
    private LivingEntity target;

    public PokemonAttackGoal(PokemonEntity pokemonEntity, double speedModifier) {
        this.pokemonEntity = pokemonEntity;
        this.speedModifier = speedModifier;

        attackRadiusSqr = 64;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }

    protected void setAttackTime(int i) {
        ((PokemonInterface) pokemonEntity).setAttackTime(i);
    }

    protected int getAttackTime() {
        return ((PokemonInterface) pokemonEntity).getAttackTime();
    }

    protected void resetAttackTime(double d) {
        PokemonAttackEffect.resetAttackTime(pokemonEntity, d);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = pokemonEntity.getOwner();
        if (owner == null) {
            if (ticksUntilNewAngerParticle < 1) {
                CobblemonFightOrFlight.PokemonEmoteAngry(pokemonEntity);
                ticksUntilNewAngerParticle = 10;
            } else {
                ticksUntilNewAngerParticle = ticksUntilNewAngerParticle - 1;
            }

            if (ticksUntilNewAngerCry < 1) {
                pokemonEntity.cry();
                ticksUntilNewAngerCry = 100 + (int) (Math.random() * 200);
            } else {
                ticksUntilNewAngerCry = ticksUntilNewAngerCry - 1;
            }
        }
        ticksUntilNextPathFinding = Math.max(ticksUntilNextPathFinding - 1, 0);
        if (PokemonUtils.shouldMelee(pokemonEntity)) {
            if (ticksUntilNextPathFinding <= 0) {
                ticksUntilNextPathFinding = 4 + pokemonEntity.getRandom().nextInt(7);
                var distance = pokemonEntity.distanceTo(target);
                if (distance > 256) {
                    ticksUntilNextPathFinding += 10;
                }
                if (!pokemonEntity.getNavigation().moveTo(target, speedModifier)) {
                    ticksUntilNextPathFinding += 15;
                }
            }
            checkAndPerformAttack(target);
        } else if (PokemonUtils.shouldShoot(pokemonEntity)) {
            rangeAttackTick();
        }
        changeMoveSpeed();
    }

    @Override
    public boolean canUse() {
        if (PokemonUtils.moveCommandAvailable(pokemonEntity) || pokemonEntity.getPokemon().getState() instanceof ShoulderedState) {
            return false;
        }
        if (PokemonUtils.shouldShoot(pokemonEntity) || PokemonUtils.shouldMelee(pokemonEntity)) {
            LivingEntity livingEntity = pokemonEntity.getTarget();
            if (livingEntity != null && livingEntity.isAlive() && PokemonUtils.shouldFightTarget(pokemonEntity)) {
                target = livingEntity;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !(PokemonUtils.shouldShoot(pokemonEntity) || PokemonUtils.shouldMelee(pokemonEntity))) {
            return false;
        }
        return (canUse() || !pokemonEntity.getNavigation().isDone()) && !isTargetInBattle();
    }

    @Override
    public void stop() {
        target = null;
        seeTime = 0;
        pokemonEntity.getNavigation().stop();
    }

    private void changeMoveSpeed() {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle && isTargetInBattle()) {
            pokemonEntity.getNavigation().setSpeedModifier(0);
        } else {
            pokemonEntity.getNavigation().setSpeedModifier(speedModifier);
        }
    }

    public boolean isTargetInBattle() {
        if (pokemonEntity.getTarget() instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }

    protected boolean canPerformAttack(LivingEntity entity) {
        return getAttackTime() == 0 && pokemonEntity.isWithinMeleeAttackRange(entity) && pokemonEntity.getSensing().hasLineOfSight(entity);
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            resetAttackTime(0);
            PokemonAttackEffect.resetAttackTime(pokemonEntity, 1);
            pokemonDoHurtTarget(target);
        }
    }

    public boolean pokemonDoHurtTarget(Entity hurtTarget) {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle) {
            if (isTargetInBattle()) {
                return false;
            }
        }
        //CobblemonFightOrFlight.LOGGER.info("Trying to use melee attack");
        if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, hurtTarget)) {
            Move move = PokemonUtils.getMove(pokemonEntity);
            if (move != null) {
                if (Arrays.stream(CobblemonFightOrFlight.moveConfig().self_centered_aoe_moves).toList().contains(move.getName())) {
                    PokemonAttackEffect.dealAoEDamage(pokemonEntity, pokemonEntity, true, PokemonUtils.isMeleeAttackMove(move));
                    if (PokemonUtils.isPhysicalMove(move)) {
                        PokemonUtils.sendAnimationPacket(pokemonEntity, "physical");
                    } else {
                        PokemonUtils.sendAnimationPacket(pokemonEntity, "special");
                    }
                    return true;
                }
            }
            PokemonUtils.sendAnimationPacket(pokemonEntity, "physical");
            return PokemonAttackEffect.pokemonAttack(pokemonEntity, hurtTarget);
        }

        return false;
    }

    private void rangeAttackTick() {
        double d = pokemonEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean bl = pokemonEntity.getSensing().hasLineOfSight(target);
        if (bl) {
            ++seeTime;
        } else {
            seeTime = 0;
            resetAttackTime(d);
        }
        if (!(d > (double) attackRadiusSqr) && seeTime >= 5 && bl) {
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
            if (d > (double) (attackRadiusSqr * 0.8F)) {
                strafingBackwards = false;
            } else if (d < (double) (attackRadiusSqr * 0.2F)) {
                strafingBackwards = true;
            }
            pokemonEntity.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            Entity vehicle = pokemonEntity.getControlledVehicle();
            if (vehicle instanceof Mob mob) {
                mob.lookAt(pokemonEntity, 30.0F, 30.0F);
            }
        }
        pokemonEntity.getLookControl().setLookAt(target);
        if (getAttackTime() == 7 && (((PokemonInterface) pokemonEntity).usingSound())) {
            PokemonUtils.createSonicBoomParticle(pokemonEntity, target);
        }
        if (getAttackTime() % 5 == 0 && (((PokemonInterface) pokemonEntity).usingMagic())) {
            PokemonAttackEffect.makeMagicAttackParticle(pokemonEntity, target);
        }
        if (getAttackTime() == 0) {
            if (!bl) {
                return;
            }
            resetAttackTime(d);
            performRangedAttack(target);
        } else if (getAttackTime() < 0) {
            resetAttackTime(d);
        }
    }

    protected void performRangedAttack(LivingEntity target) {
        PokemonAttackEffect.pokemonPerformRangedAttack(pokemonEntity, target);
    }
}
