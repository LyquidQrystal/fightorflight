package me.rufia.fightorflight.entity.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FOFMemories {
    private static final Map<String, MemoryModuleType> memories;

    static {
        memories = new HashMap<>();
    }

    public static MemoryModuleType<Integer> ATTACK_MODE = register("attack_mode", PrimitiveCodec.INT);

    public static <T> MemoryModuleType<T> register(String id, Codec<T> codec) {
        var m = new MemoryModuleType<>(Optional.of(codec));
        memories.putIfAbsent(id, m);
        return m;
    }
}
