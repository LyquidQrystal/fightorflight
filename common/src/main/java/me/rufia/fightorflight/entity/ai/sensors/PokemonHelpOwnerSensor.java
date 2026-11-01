package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fights back when the owner is attacked / inherits the owner's target. Priority
 * {@link FOFTargetPriority#OWNER_DEFENSE}, below a player order: once the player ordered an attack,
 * the owner being hit does not drag the Pokemon away from it.
 */
public class PokemonHelpOwnerSensor extends FOFPrioritizedTargetSensor {
    private static final int REFRESH_RATE = 1200;

    public PokemonHelpOwnerSensor() {
        super(10);
    }

    @Override
    public FOFTargetPriority priority() {
        return FOFTargetPriority.OWNER_DEFENSE;
    }

    @Override
    protected boolean wantsTarget(PokemonEntity entity) {
        return CobblemonFightOrFlight.commonConfig().do_pokemon_defend_owner && entity.getOwner() != null;
    }

    @Override
    protected @Nullable LivingEntity findTarget(PokemonEntity pokemonEntity, @NotNull NearestVisibleLivingEntities visibleMobs) {
        var pokemon = (PokemonInterface) pokemonEntity;
        //inherit the owner's latest target, expires on its own once it goes stale or is cleared
        int ownerLastHurtTick = pokemon.getOwnerLastHurtTick();
        var ownerLastHurtTarget = pokemon.getOwnerLastHurt();
        if (ownerLastHurtTarget != null && ownerLastHurtTarget.isAlive() && pokemonEntity.tickCount - ownerLastHurtTick < REFRESH_RATE) {
            return ownerLastHurtTarget;
        }
        //otherwise look for whoever is attacking the owner
        LivingEntity owner = pokemonEntity.getOwner();
        LivingEntity nearestAttacker = visibleMobs.findClosest(livingEntity -> {
            var lastHurtByMob = livingEntity.getLastHurtByMob();
            if (lastHurtByMob instanceof PokemonEntity attacker) {
                if (PokemonUtils.tryToAvoidWildShiny(attacker)) {
                    return false;
                }
            }
            return lastHurtByMob != null && lastHurtByMob.is(owner);
        }).orElse(null);
        pokemon.setOwnerLastHurt(nearestAttacker);
        return nearestAttacker;
    }
}
