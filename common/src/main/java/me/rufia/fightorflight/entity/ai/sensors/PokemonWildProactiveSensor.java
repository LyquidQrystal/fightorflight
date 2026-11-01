package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Unprovoked aggro of a wild Pokemon. Lowest priority ({@link FOFTargetPriority#WILD_PROACTIVE}),
 * only fills an empty slot and is always beaten by every other source.
 */
public class PokemonWildProactiveSensor extends FOFPrioritizedTargetSensor {
    public PokemonWildProactiveSensor() {
        super(10);
    }

    @Override
    public FOFTargetPriority priority() {
        return FOFTargetPriority.WILD_PROACTIVE;
    }

    @Override
    protected boolean wantsTarget(PokemonEntity entity) {
        if (!PokemonUtils.WildPokemonCanPerformUnprovokedAttack(entity)) {
            return false;
        }
        if (CobblemonFightOrFlight.getFightOrFlightCoefficient(entity) < CobblemonFightOrFlight.AUTO_AGGRO_THRESHOLD()) {
            return false;
        }
        //no unprovoked attacks once it is bright
        return !(CobblemonFightOrFlight.commonConfig().light_dependent_unprovoked_attack
                && entity.getLightLevelDependentMagicValue() >= 0.5f);
    }

    @Override
    protected @Nullable LivingEntity findTarget(PokemonEntity entity, @NotNull NearestVisibleLivingEntities visible) {
        return visible.findClosest(livingEntity -> {
            if (livingEntity instanceof ServerPlayer player) {
                if (player.isCreative() || player.isSpectator()) {
                    return false;
                }
                return !PokemonUtils.playerHasBattle(player);
            }
            return false;
        }).orElse(null);
    }
}
