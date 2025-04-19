package me.rufia.fightorflight.goals;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.entity.PokemonAttackEffect;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import java.util.Arrays;


public class PokemonMeleeAttackGoal extends MeleeAttackGoal {
    private final double speedModifier;
    public int ticksUntilNewAngerParticle = 0;
    public int ticksUntilNewAngerCry = 0;

    public PokemonMeleeAttackGoal(PokemonEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.speedModifier = speedModifier;
    }

    public void tick() {
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        LivingEntity owner = pokemonEntity.getOwner();
        if (owner == null) {
            if (ticksUntilNewAngerParticle < 1) {
                CobblemonFightOrFlight.PokemonEmoteAngry(this.mob);
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
        super.tick();
        changeMoveSpeed();
    }

    private void changeMoveSpeed() {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle && isTargetInBattle()) {
            this.mob.getNavigation().setSpeedModifier(0);
        } else {

            this.mob.getNavigation().setSpeedModifier(this.speedModifier);
        }
    }

    public boolean isTargetInBattle() {
        if (this.mob.getTarget() instanceof ServerPlayer targetAsPlayer) {
            return BattleRegistry.INSTANCE.getBattleByParticipatingPlayer(targetAsPlayer) != null;
        }
        return false;
    }

    public boolean canUse() {
        if (mob instanceof PokemonEntity pokemonEntity) {
            return !PokemonUtils.moveCommandAvailable(pokemonEntity) && PokemonUtils.shouldMelee(pokemonEntity) && PokemonUtils.shouldFightTarget(pokemonEntity) && super.canUse();
        }
        return false;
    }

    public boolean canContinueToUse() {
        return PokemonUtils.shouldFightTarget((PokemonEntity) mob) && super.canContinueToUse() && !PokemonUtils.moveCommandAvailable((PokemonEntity) mob);
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (canPerformAttack(target)) {
            if (mob instanceof PokemonEntity pokemonEntity) {
                if (((PokemonInterface) pokemonEntity).getAttackTime() == 0) {
                    this.resetAttackCooldown();
                    pokemonDoHurtTarget(target);
                    PokemonAttackEffect.resetAttackTime(pokemonEntity, 1);
                }
            }
        }
    }

    public boolean pokemonDoHurtTarget(Entity hurtTarget) {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_attack_in_battle) {
            if (isTargetInBattle()) {
                return false;
            }
        }
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;

        if (!PokemonUtils.pokemonTryForceEncounter(pokemonEntity, hurtTarget)) {
            Move move = PokemonUtils.getMove(pokemonEntity);
            if (move != null) {
                if (Arrays.stream(CobblemonFightOrFlight.moveConfig().self_centered_aoe_moves).toList().contains(move.getName())) {
                    PokemonAttackEffect.dealAoEDamage(pokemonEntity, pokemonEntity, true);
                    if (PokemonUtils.isMeleeAttackMove(move)) {
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
}
