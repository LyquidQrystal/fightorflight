package me.rufia.fightorflight.goals.targeting;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.activestate.ShoulderedState;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.TargetingWhitelist;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

@Deprecated
public class PokemonOwnerHurtByTargetGoal extends TargetGoal {
    private final PokemonEntity pokemonEntity;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public PokemonOwnerHurtByTargetGoal(PokemonEntity pokemonEntity) {
        super(pokemonEntity, false);
        this.pokemonEntity = pokemonEntity;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        if (!CobblemonFightOrFlight.commonConfig().do_pokemon_defend_owner) {
            return false;
        }

        LivingEntity owner = this.pokemonEntity.getOwner();

        if (owner != null && !this.pokemonEntity.isBusy()) {
            if (pokemonEntity.getPokemon().getState() instanceof ShoulderedState) {
                return false;
            }
            this.ownerLastHurtBy = owner.getLastHurtByMob();
            int i = owner.getLastHurtByMobTimestamp();
            if (ownerLastHurtBy != null && TargetingWhitelist.getWhitelist(pokemonEntity).contains(ownerLastHurtBy.getEncodeId())) {
                return false;
            }
            return i != this.timestamp &&
                    this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.pokemonEntity.wantsToAttack(this.ownerLastHurtBy, owner);
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity owner = this.pokemonEntity.getOwner();
        if (owner != null) {
            this.timestamp = owner.getLastHurtByMobTimestamp();
        }

        super.start();
    }
}
