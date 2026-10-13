package me.rufia.fightorflight.client.hud.moveinfo;

import com.mojang.blaze3d.vertex.PoseStack;
import me.rufia.fightorflight.CobblemonFightOrFlightClient;
import me.rufia.fightorflight.utils.msg.FOFMsg;
import me.rufia.fightorflight.utils.msg.FOFSwitchMoveMsg;
import me.rufia.fightorflight.utils.msg.FactionColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public class MoveInfoRenderer {
    public static float TEXT_SIZE = 0.5f;

    public static void render(GuiGraphics graphics, float tickDelta) {
        int msgAmount = CobblemonFightOrFlightClient.getMsgCount();
        Minecraft minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        //TODO Config to disable it
        if (player == null || msgAmount == 0) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }
        var screenWidth = minecraft.getWindow().getGuiScaledWidth();
        var screenHeight = minecraft.getWindow().getGuiScaledHeight();
        Font font = minecraft.font;
        float info_width_relative = 0.2f;
        float info_height_relative = 0.1f;
        float origin_x_relative = 0.75f;
        float origin_y_relative = 0.25f;

        int infoWidth = (int) (screenWidth * info_width_relative);
        int infoHeight = (int) (screenHeight * info_height_relative);
        int originX = (int) (screenWidth * origin_x_relative);
        int originY = (int) (screenHeight * origin_y_relative);

        for (int i = 0; i < msgAmount; ++i) {
            renderMoveInfo(graphics, font, originX, originY, infoWidth, infoHeight, i, tickDelta);
        }

        CobblemonFightOrFlightClient.updateMsgList();
    }

    public static void renderMoveInfo(GuiGraphics graphics, Font font, int originX, int originY, int width, int height, int index, float tickDelta) {
        FOFMsg msg = CobblemonFightOrFlightClient.getMsg(index);
        if (!(msg instanceof FOFSwitchMoveMsg switchMoveMsg)) {
            return;
        }
        int guiTick = Minecraft.getInstance().gui.getGuiTicks();
        int horizontalMovement = getHorizontalMovement(switchMoveMsg, guiTick + tickDelta, width);
        int x1 = originX + horizontalMovement;
        int y1 = originY + index * height;
        int x2 = x1 + width + horizontalMovement;
        int y2 = y1 + height;
        //graphics.fill(x1, y1, x2, y2, 0xA040A0C0);
        int alphaValue = getAlpha(switchMoveMsg, guiTick);
        FactionColor factionColor = FactionColor.getColorFromFaction(switchMoveMsg.getFaction());
        graphics.fillGradient(x1, y1, x2, y2 - 2, modifyAlphaValue(alphaValue, factionColor.getTopColor()), factionColor.getBottomColor());
        graphics.fill(x1, y1, x1 + 2, y2 - 2, factionColor.getSidebarColor());
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(TEXT_SIZE, TEXT_SIZE, 1);
        int textX = x1 + width / 10;
        int textY = y1 + height / 4;
        graphics.drawWordWrap(font, switchMoveMsg.getMessageText(), (int) (textX / TEXT_SIZE), (int) (textY / TEXT_SIZE), (int) (width * 0.8 / TEXT_SIZE), 0xFFFFFF);
        //graphics.drawCenteredString(font, msg.getMessageText(), (int) (textX / TEXT_SIZE), (int) (textY / TEXT_SIZE), 0xFFFFFF);
        poseStack.popPose();
    }

    public static int getAlpha(FOFMsg msg, int guiTick) {
        int livingTime = guiTick - msg.getCreateTime();
        int remainingTime = msg.getDuration() - livingTime;
        if (remainingTime > 80) {
            return 255;
        }
        return Mth.lerpInt(Mth.sqrt(remainingTime / 80f), 32, 255);
    }

    public static int modifyAlphaValue(int alpha, int colorCode) {
        int alphaRemoved = colorCode & 0x00FFFFFF;
        return ((alpha & 255) << 24) | alphaRemoved;
    }

    public static int getHorizontalMovement(FOFMsg msg, float guiTickWithDelta, int width) {
        float livingTime = guiTickWithDelta - msg.getCreateTime();
        float remainingTime = msg.getDuration() - livingTime;
        if (livingTime < 10) {
            return (int) (width * (10f - livingTime) / 20f);
        }
        if (remainingTime < 10) {
            return (int) (width * (10f - remainingTime) / 20f);
        }
        return 0;
    }
}
