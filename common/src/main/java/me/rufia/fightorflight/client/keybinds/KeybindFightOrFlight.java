package me.rufia.fightorflight.client.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public class KeybindFightOrFlight {
    public static List<KeyMapping> bindings = new ArrayList<>();
    public static KeyMapping START_BATTLE;
    public static MoveSlotKeybind MOVE_SLOT_1;
    public static MoveSlotKeybind MOVE_SLOT_2;
    public static MoveSlotKeybind MOVE_SLOT_3;
    public static MoveSlotKeybind MOVE_SLOT_4;
    public static List<CommandKeybind> commandKeybinds = new ArrayList<>();

    static {
        START_BATTLE = new KeyMapping("key.fightorflight.startbattle", InputConstants.Type.KEYSYM, InputConstants.KEY_G, KeybindCategories.FOF);
        MOVE_SLOT_1 = new MoveSlotKeybind("key.fightorflight.moveslot1", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KeybindCategories.FOF_POKESTAFF, 0);
        MOVE_SLOT_2 = new MoveSlotKeybind("key.fightorflight.moveslot2", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KeybindCategories.FOF_POKESTAFF, 1);
        MOVE_SLOT_3 = new MoveSlotKeybind("key.fightorflight.moveslot3", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KeybindCategories.FOF_POKESTAFF, 2);
        MOVE_SLOT_4 = new MoveSlotKeybind("key.fightorflight.moveslot4", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KeybindCategories.FOF_POKESTAFF, 3);
        bindings.add(START_BATTLE);
        bindings.add(MOVE_SLOT_1);
        bindings.add(MOVE_SLOT_2);
        bindings.add(MOVE_SLOT_3);
        bindings.add(MOVE_SLOT_4);
        for (PokeStaffComponent.CMDMODE cmdmode : PokeStaffComponent.CMDMODE.values()) {
            CommandKeybind keybind = new CommandKeybind("key.fightorflight.cmdmode." + cmdmode.name().toLowerCase(), InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KeybindCategories.FOF_POKESTAFF, cmdmode);
            commandKeybinds.add(keybind);
            bindings.add(keybind);
        }
    }
}
