package me.rufia.fightorflight.client.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class MoveSlotKeybind extends KeyMapping {
    private final int MOVE_SLOT;

    public MoveSlotKeybind(String name, InputConstants.Type type, int keyCode, String category, int moveSlot) {
        super(name, type, keyCode, category);
        MOVE_SLOT = moveSlot;
    }

    public int getMoveSlot() {
        return MOVE_SLOT;
    }
}
