package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@Deprecated
public class PokemonProactiveTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public float safeDistanceSqr;

    public PokemonProactiveTargetGoal(Mob mob, Class<T> targetType, float safeDistanceSqr, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, randomInterval, mustSee, mustReach, targetPredicate);
        this.safeDistanceSqr = safeDistanceSqr;
    }

    public boolean canUse() {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_defend_proactive) {
            return false;
        }
        PokemonEntity pokemonEntity = (PokemonEntity) this.mob;
        if (pokemonEntity.getPokemon().getState() instanceof ShoulderedState) {
            return false;
        }
        if (!pokemonEntity.getPokemon().isPlayerOwned()) {
            return false;
        }

        return super.canUse();
    }

    protected void findTarget() {
        super.findTarget();
        if (!(mob instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        /*
        if (this.target != null) {
            if (this.target.distanceToSqr(this.mob) > safeDistanceSqr) {
                this.target = null;
            } else if (TargetingWhitelist.getWhitelist(pokemonEntity).contains(target.getEncodeId())) {
                this.target = null;
            } else if (target instanceof Creeper && !(CobblemonFightOrFlight.commonConfig().do_pokemon_defend_creeper_proactive)) {
                this.target = null;
            } else if (CobblemonFightOrFlight.commonConfig().pokemon_proactive_level == 1) {
                if (target instanceof NeutralMob neutralMob) {
                    var mobTarget = neutralMob.getTarget();
                    if (!Objects.equals(mobTarget, pokemonEntity.getOwner())) {
                        this.target = null;
                    }
                }
            }
        }*/
    }
}
