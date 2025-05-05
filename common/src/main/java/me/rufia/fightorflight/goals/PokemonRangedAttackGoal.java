package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.entity.projectile.AbstractPokemonProjectile;
import me.rufia.fightorflight.entity.projectile.PokemonArrow;
import me.rufia.fightorflight.entity.projectile.PokemonBullet;
import me.rufia.fightorflight.entity.projectile.PokemonTracingBullet;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;

public class PokemonRangedAttackGoal extends PokemonAttackGoal {
    private final PokemonEntity pokemonEntity;
    private final LivingEntity livingEntity;
    @Nullable
    private LivingEntity target;
    private final double speedModifier;
    private int seeTime;

    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    private final float attackRadius;
    private final float attackRadiusSqr;

    public PokemonRangedAttackGoal(LivingEntity pokemonEntity, double speedModifier, float attackRadius) {
        setAttackTime(-1);
        this.livingEntity = pokemonEntity;
        if (!(pokemonEntity instanceof PokemonEntity)) {
            throw new IllegalArgumentException("PokemonRangedAttackGoal requires a PokemonEntity");
        } else {
            this.pokemonEntity = (PokemonEntity) pokemonEntity;
            this.speedModifier = speedModifier;

            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;

            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
        }
    }

    public boolean canUse() {
        if (!PokemonUtils.shouldShoot(pokemonEntity) || PokemonUtils.moveCommandAvailable(pokemonEntity)) {
            return false;
        }
        if (pokemonEntity.getPokemon().getState() instanceof ShoulderedState) {
            return false;
        }
        LivingEntity livingEntity = this.pokemonEntity.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            this.target = livingEntity;
            return PokemonUtils.shouldFightTarget(pokemonEntity);
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        if (target == null || !PokemonUtils.shouldShoot(pokemonEntity)) {
            return false;
        }
        return (this.canUse() || !this.pokemonEntity.getNavigation().isDone()) && !isTargetInBattle();
    }

    public void stop() {
        this.target = null;
        this.seeTime = 0;
        pokemonEntity.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean isTargetInBattle() {
        if (this.pokemonEntity.getTarget() instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }

    public void tick() {
        super.tick();
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle) {
            if (isTargetInBattle()) {
                this.pokemonEntity.getNavigation().setSpeedModifier(0);
            }
        }

        if (target != null) {
            double d = this.pokemonEntity.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean bl = this.pokemonEntity.getSensing().hasLineOfSight(this.target);
            if (bl) {
                ++this.seeTime;
            } else {
                seeTime = 0;
                resetAttackTime(d);
            }
            if (!(d > (double) this.attackRadiusSqr) && this.seeTime >= 5 && bl) {
                this.pokemonEntity.getNavigation().stop();
                ++strafingTime;
            } else {
                this.pokemonEntity.getNavigation().moveTo(this.target, this.speedModifier);
                strafingTime = -1;
            }
            if (this.strafingTime >= 10) {
                if ((double) this.pokemonEntity.getRandom().nextFloat() < 0.3) {
                    this.strafingClockwise = !this.strafingClockwise;
                }
                if ((double) this.pokemonEntity.getRandom().nextFloat() < 0.3) {
                    this.strafingBackwards = !this.strafingBackwards;
                }
                this.strafingTime = 0;
            }
            if (this.strafingTime > -1) {
                if (d > (double) (this.attackRadiusSqr * 0.8F)) {
                    this.strafingBackwards = false;
                } else if (d < (double) (this.attackRadiusSqr * 0.2F)) {
                    this.strafingBackwards = true;
                }
                this.pokemonEntity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                Entity vehicle = this.pokemonEntity.getControlledVehicle();
                if (vehicle instanceof Mob mob) {
                    mob.lookAt(livingEntity, 30.0F, 30.0F);
                }
            }
            this.pokemonEntity.getLookControl().setLookAt(this.target);
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
                this.performRangedAttack(this.target);
            } else if (getAttackTime() < 0) {
                resetAttackTime(d);
            }
        }
    }

    @Override
    protected PokemonEntity getPokemonEntity() {
        return pokemonEntity;
    }

    protected void performRangedAttack(LivingEntity target) {
        PokemonAttackEffect.pokemonPerformRangedAttack(pokemonEntity, target);
    }
}
