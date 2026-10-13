package me.rufia.fightorflight;

import me.rufia.fightorflight.utils.msg.FOFMsg;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;


public class CobblemonFightOrFlightClient {
    private static final List<FOFMsg> CLIENT_MESSAGE_LIST = new ArrayList<>();
    private static boolean shouldRenderMessageList;

    public static void addMsg(FOFMsg msg) {
        int msgCount = 5;
        if (CLIENT_MESSAGE_LIST.size() >= msgCount) {
            CLIENT_MESSAGE_LIST.removeFirst();
        }
        CLIENT_MESSAGE_LIST.add(msg);
    }

    public static void addMsgDirect(FOFMsg msg) {
        CLIENT_MESSAGE_LIST.add(msg);
    }

    public static int getMsgCount() {
        return CLIENT_MESSAGE_LIST.size();
    }

    public static FOFMsg getMsg(int index) {
        if (index >= CLIENT_MESSAGE_LIST.size() || index < 0) {
            return null;
        }
        return CLIENT_MESSAGE_LIST.get(index);
    }

    public static void updateMsgList() {
        int guiTick = Minecraft.getInstance().gui.getGuiTicks();
        CLIENT_MESSAGE_LIST.removeIf((msg -> !msg.isAvailable(guiTick)));
    }

    public static void changeShouldRenderMessageList() {
        shouldRenderMessageList = !shouldRenderMessageList;
    }

    public static boolean isShouldRenderMessageList() {
        return shouldRenderMessageList;
    }
}
