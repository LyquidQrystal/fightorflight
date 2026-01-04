package me.rufia.fightorflight.entity.ai.sensors;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.Set;

public class PokeStaffWalkTargetSensor extends Sensor<PokemonEntity> {

    @Override
    protected void doTick(ServerLevel level, PokemonEntity entity) {
        if (!CobblemonFightOrFlight.commonConfig().can_use_poke_staff) {
            return;
        }
        var owner = entity.getOwner();
        if (owner instanceof Player && !entity.isBusy()) {
            BlockPos blockPos = ((PokemonInterface) entity).getTargetBlockPos();
            if (blockPos != BlockPos.ZERO) {
                float speedMultiplier = 0.75f * PokemonUtils.calculateExtraSpeed(entity);
                int closeEnoughDist = 2;
                entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, speedMultiplier, closeEnoughDist));

                if (entity.distanceToSqr(blockPos.getCenter()) < closeEnoughDist) {
                    PokemonUtils.finishMoving(entity);
                }
            }
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.WALK_TARGET);
    }
}
