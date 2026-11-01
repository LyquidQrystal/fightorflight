package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.FOFTargetPriority;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a grudge after a failed capture. Priority {@link FOFTargetPriority#PROVOKED}: beats auto
 * battle and wild unprovoked aggro, yields to player orders and owner defense.
 */
public class PokemonCaughtBySensor extends FOFPrioritizedTargetSensor {
    public PokemonCaughtBySensor() {
        super(10);
    }

    @Override
    public FOFTargetPriority priority() {
        return FOFTargetPriority.PROVOKED;
    }

    @Override
    protected boolean wantsTarget(PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().failed_capture_counted_as_provocation) {
            return false;
        }
        return entity.getOwner() == null;
    }

    @Override
    protected @Nullable LivingEntity findTarget(PokemonEntity entity, @NotNull NearestVisibleLivingEntities visible) {
        int catcherId = ((PokemonInterface) entity).getCapturedBy();
        if (catcherId == 0) {
            return null;
        }
        return visible.findClosest(livingEntity -> livingEntity.getId() == catcherId).orElse(null);
    }
}
