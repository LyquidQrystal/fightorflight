package me.rufia.fightorflight.client.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import net.minecraft.client.KeyMapping;

public class CommandKeybind extends KeyMapping {
    private final PokeStaffComponent.CMDMODE cmdmode;

    public CommandKeybind(String name, InputConstants.Type type, int keyCode, String category, PokeStaffComponent.CMDMODE cmdmode) {
        super(name, type, keyCode, category);
        this.cmdmode = cmdmode;
    }

    public PokeStaffComponent.CMDMODE getCmdmode() {
        return cmdmode;
    }
}
