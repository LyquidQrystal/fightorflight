package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.behavior.PokemonBehaviorData;
import me.rufia.fightorflight.utils.PokemonUtils;
import me.rufia.fightorflight.utils.TargetingWhitelist;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

@Deprecated
public class PokemonNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public int ticksUntilNewAngerParticle = 0;
    public float safeDistanceSqr = 36;
    private int ticksNextDataBehaviorCheckCycle = 0;

    public PokemonNearestAttackableTargetGoal(Mob mob, Class<T> targetType, float safeDistanceSqr, boolean mustSee, boolean mustReach) {
        super(mob, targetType, mustSee, mustReach);
        this.safeDistanceSqr = safeDistanceSqr;
    }

    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
            if (!PokemonUtils.WildPokemonCanPerformUnprovokedAttack(pokemonEntity)) {
                return false;
            }
            if (CobblemonFightOrFlight.commonConfig().enable_datapack_driven_behavior) {
                ++ticksNextDataBehaviorCheckCycle;
                if (ticksNextDataBehaviorCheckCycle == 20) {
                    ticksNextDataBehaviorCheckCycle = 0;
                }
                String speciesName = pokemonEntity.getPokemon().getSpecies().getName();
                if (ticksNextDataBehaviorCheckCycle == 11) {
                    if (PokemonBehaviorData.behaviorData.containsKey(speciesName)) {
                        var dataList = PokemonBehaviorData.behaviorData.get(speciesName);
                        for (PokemonBehaviorData data : dataList) {
                            if (data.getType().equals("proactive")) {
                                if (!data.check(pokemonEntity)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            if (CobblemonFightOrFlight.getFightOrFlightCoefficient(pokemonEntity) <= CobblemonFightOrFlight.AUTO_AGGRO_THRESHOLD() || (CobblemonFightOrFlight.commonConfig().light_dependent_unprovoked_attack && pokemonEntity.getLightLevelDependentMagicValue() >= 0.5f)) {
                return false;
            } else {
                if (ticksUntilNewAngerParticle < 1) {
                    CobblemonFightOrFlight.PokemonEmoteAngry(this.mob);
                    ticksUntilNewAngerParticle = 25;
                } else {
                    ticksUntilNewAngerParticle = ticksUntilNewAngerParticle - 1;
                }
            }

            this.findTarget();
            return this.target != null;
        }
    }

    @Override
    protected void findTarget() {
        super.findTarget();

        if (target != null) {
            if (this.target.distanceToSqr(this.mob) > safeDistanceSqr) {
                this.target = null;
            } else if (TargetingWhitelist.getWhitelist((PokemonEntity) this.mob).contains(target.getEncodeId())) {
                this.target = null;
            }
        }
    }
}
