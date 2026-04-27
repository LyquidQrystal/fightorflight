package me.rufia.fightorflight.platform.fabric;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class EffectRegisterImpl {
    public static Holder<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.MODID, name), effect.get());
    }
}
