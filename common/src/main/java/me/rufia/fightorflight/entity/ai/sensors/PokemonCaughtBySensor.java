package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Set;

public class PokemonCaughtBySensor extends Sensor<PokemonEntity> {
    public PokemonCaughtBySensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().failed_capture_counted_as_provocation) {
            return;
        }
        if (entity.getOwner() == null) {
            entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(visibleLivingEntities -> setNearestTarget(entity, visibleLivingEntities));
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET);
    }

    private void setNearestTarget(PokemonEntity pokemonEntity, NearestVisibleLivingEntities visibleMobs) {
        int catcherUUID = ((PokemonInterface) pokemonEntity).getCapturedBy();
        if (pokemonEntity.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)) {
            return;
        }
        var targetOpt = visibleMobs.findClosest(livingEntity -> catcherUUID == livingEntity.getId());
        targetOpt.ifPresent(target -> pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target));
    }
}
