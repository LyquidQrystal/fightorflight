package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PokemonWildProactiveSensor extends Sensor<PokemonEntity> {
    public PokemonWildProactiveSensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!PokemonUtils.WildPokemonCanPerformUnprovokedAttack(entity)) {
            return;
        }
        if (CobblemonFightOrFlight.getFightOrFlightCoefficient(entity) <= CobblemonFightOrFlight.AUTO_AGGRO_THRESHOLD() || (CobblemonFightOrFlight.commonConfig().light_dependent_unprovoked_attack && entity.getLightLevelDependentMagicValue() >= 0.5f)) {
            return;
        }
        entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(visibleLivingEntities -> findTarget(entity, visibleLivingEntities));
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.ATTACK_TARGET);
    }

    private void findTarget(PokemonEntity pokemonEntity, NearestVisibleLivingEntities visibleLivingEntities) {
        var nearestPlayer = visibleLivingEntities.findClosest(livingEntity -> {
            if (livingEntity instanceof Player player) {
                return !(player.isCreative() || player.isSpectator());
            }
            return false;
        });
        if (pokemonEntity.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT))
            nearestPlayer.ifPresent(player -> pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, player));
    }

}
