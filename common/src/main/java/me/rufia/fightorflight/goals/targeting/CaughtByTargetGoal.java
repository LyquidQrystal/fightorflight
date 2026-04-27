package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.TargetingWhitelist;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

import java.util.EnumSet;

@Deprecated
public class CaughtByTargetGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private LivingEntity lastCaughtByMob;

    public CaughtByTargetGoal(Mob mob) {
        super(mob, true, false);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (!CobblemonFightOrFlight.commonConfig().failed_capture_counted_as_provocation) {
            return false;
        }
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        if (pokemonEntity.getOwner() != null) {
            return false;
        }
        int mobID = ((PokemonInterface) pokemonEntity).getCapturedBy();
        if (mobID != 0) {
            Entity target = mob.level().getEntity(mobID);
            if (target != null && TargetingWhitelist.getWhitelist(pokemonEntity).contains(target.getEncodeId())) {
                return false;//I don't know who will list the player in the list, but just let it happen.
            }
            if (target instanceof LivingEntity livingEntity) {
                lastCaughtByMob = livingEntity;
            }
        }
        if (lastCaughtByMob != null) {
            if (lastCaughtByMob.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                return this.canAttack(lastCaughtByMob, HURT_BY_TARGETING);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(lastCaughtByMob);
        this.targetMob = this.mob.getTarget();
        this.mob.setLastHurtByMob(this.mob.getTarget());
        if (this.mob.getTarget() instanceof Player) {
            this.mob.setLastHurtByPlayer((Player) this.mob.getTarget());
        }
        this.unseenMemoryTicks = 300;
        super.start();
    }
}
