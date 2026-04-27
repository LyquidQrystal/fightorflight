package me.rufia.fightorflight.platform.neoforge;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EffectRegisterImpl {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS=DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CobblemonFightOrFlight.MODID);
    public static Holder<MobEffect> register(String name, Supplier<MobEffect> effect) {
        return MOB_EFFECTS.register(name,effect);
    }
}
