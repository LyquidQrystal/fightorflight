package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.CobblemonMemories;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PokemonHelpOwnerSensor extends Sensor<PokemonEntity> {
    public PokemonHelpOwnerSensor() {
        super(10);
        //CobblemonFightOrFlight.LOGGER.info("FOF Sensor created: pokemon_help_owner_fof");
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        CobblemonFightOrFlight.LOGGER.info("Sensor ticking");
        var owner = entity.getOwner();
        if (owner != null) {
            entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent(visibleLivingEntities -> {
                setNearestAttacker(entity, visibleLivingEntities, owner);
            });
        }
    }

    @Override
    public @NotNull Set<MemoryModuleType<?>> requires() {
        return Set.of(CobblemonMemories.NEAREST_VISIBLE_ATTACKER);
    }

    private void setNearestAttacker(PokemonEntity pokemonEntity, NearestVisibleLivingEntities visibleMobs, LivingEntity owner) {
        var nearestAttacker = visibleMobs.findClosest(livingEntity -> {
            if (livingEntity instanceof ServerPlayer) {
                return false;
            }
            var lastHurtByMob = livingEntity.getLastHurtByMob();
            return lastHurtByMob != null && lastHurtByMob.is(owner);
        });
        pokemonEntity.getBrain().setMemory(CobblemonMemories.NEAREST_VISIBLE_ATTACKER, nearestAttacker);
    }
}
