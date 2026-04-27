package me.rufia.fightorflight.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

public class EffectRegister {
    @ExpectPlatform
    public static Holder<MobEffect> register(String name, Supplier<MobEffect> effect) {
        throw new AssertionError();
    }

}
