package me.rufia.fightorflight;

import me.rufia.fightorflight.utils.event.msg.FOFMsg;
import me.rufia.fightorflight.utils.handler.MsgHandler;
import net.minecraft.client.Minecraft;


public class CobblemonFightOrFlightClient {
    private static boolean shouldRenderMessageList = true;
    private static final MsgHandler handler = new MsgHandler();

    public static void addMsg(FOFMsg msg) {
        handler.addMsg(msg);
    }

    public static int getMsgCount() {
        return handler.getMsgCount();
    }

    public static FOFMsg getMsg(int index) {
        return handler.getMsg(index);
    }

    public static void updateMsgList() {
        int guiTick = Minecraft.getInstance().gui.getGuiTicks();
        handler.update(guiTick);
    }

    public static void changeShouldRenderMessageList() {
        shouldRenderMessageList = !shouldRenderMessageList;
    }

    public static boolean isShouldRenderMessageList() {
        return shouldRenderMessageList;
    }
}
