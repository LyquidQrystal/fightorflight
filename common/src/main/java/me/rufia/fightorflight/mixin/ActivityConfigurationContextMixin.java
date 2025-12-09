package me.rufia.fightorflight.mixin;

import com.cobblemon.mod.common.api.ai.ActivityConfigurationContext;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.datafixers.util.Pair;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.entity.ai.tasks.FOFBackUpIfTooClose;
import me.rufia.fightorflight.entity.ai.tasks.FOFPokemonRangeTask;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BackUpIfTooClose;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ActivityConfigurationContext.class)
public abstract class ActivityConfigurationContextMixin {
    @Final
    @Shadow(remap = false)
    private List<Pair<Integer, BehaviorControl<? super LivingEntity>>> tasks;
    @Final
    @Shadow(remap = false)
    private Activity activity;

    @Inject(method = "apply", at = @At("HEAD"), remap = false)
    private void applyMixin(LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof PokemonEntity) {
            //CobblemonFightOrFlight.LOGGER.info("{} is loading activity", entity.getName().getString());
            if (Objects.equals(activity.getName(), "fight")) {
                if (CobblemonFightOrFlight.commonConfig().use_range_attack) {
                    //CobblemonFightOrFlight.LOGGER.info("{} is loading fight activity", entity.getName().getString());
                    tasks.add(new Pair<>(0, BehaviorBuilder.triggerIf(
                            livingEntity -> {
                                if (livingEntity instanceof PokemonEntity pokemonEntity) {
                                    return PokemonUtils.shouldShoot(pokemonEntity);
                                }
                                return false;
                            },
                            FOFBackUpIfTooClose.create(5, 0.75f)
                    )));
                    tasks.add(new Pair<>(1, new FOFPokemonRangeTask()));
                    //CobblemonFightOrFlight.LOGGER.info("{} fight activity tasks count:{}", entity.getName().getString(), tasks.size());
                }
            }
        }
    }
}
