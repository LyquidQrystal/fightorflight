package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.CobblemonMemories;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.PokemonInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PokemonHelpOwnerSensor extends Sensor<PokemonEntity> {
    private static final int REFRESH_RATE = 1200;

    public PokemonHelpOwnerSensor() {
        super(10);
    }

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
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
        int ownerLastHurtTick = ((PokemonInterface) pokemonEntity).getOwnerLastHurtTick();
        var ownerLastHurtTarget = ((PokemonInterface) pokemonEntity).getOwnerLastHurt();
        if (ownerLastHurtTarget != null && ownerLastHurtTarget.isAlive() && pokemonEntity.tickCount - ownerLastHurtTick < REFRESH_RATE) {
            pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, ownerLastHurtTarget);
            return;
        }
        var nearestAttacker = visibleMobs.findClosest(livingEntity -> {
            if (livingEntity instanceof ServerPlayer) {
                return false;
            }
            var lastHurtByMob = livingEntity.getLastHurtByMob();
            boolean cond = lastHurtByMob != null && lastHurtByMob.is(owner);
            return cond;
        });
        ((PokemonInterface) pokemonEntity).setOwnerLastHurt(nearestAttacker.orElse(null));
        pokemonEntity.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, nearestAttacker);
    }
}
