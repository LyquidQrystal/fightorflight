package me.rufia.fightorflight.client.hud.moveslots;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.activestate.SentOutState;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.PokemonInterface;
import me.rufia.fightorflight.utils.PokemonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class MoveSlotsRender {
    private static final ResourceLocation TYPE_ICON_LOCATION = ResourceLocation.fromNamespaceAndPath(CobblemonFightOrFlight.COBBLEMON_MOD_ID, "textures/gui/types.png");
    private static final int TYPE_ICON_SIZE = 36;
    private static final int DRAW_SIZE = (int) (TYPE_ICON_SIZE * CobblemonFightOrFlight.visualEffectConfig().move_indicator_size);
    private static final float TEXT_SIZE = 0.5f;

    public static void render(GuiGraphics graphics, float tickDelta, Pokemon pokemon) {
        Minecraft minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (!CobblemonFightOrFlight.visualEffectConfig().enable_move_indicator) {
            return;
        }
        if (player == null || pokemon == null) {
            CobblemonFightOrFlight.LOGGER.info("Failed to render the icon.");
            return;
        }
        if (player.isSpectator()) {
            return;
        }

        var screenWidth = minecraft.getWindow().getGuiScaledWidth();
        var screenHeight = minecraft.getWindow().getGuiScaledHeight();
        var state = pokemon.getState();
        if (state instanceof SentOutState) {
            var entity = pokemon.getEntity();
            if (entity != null) {
                Move move = PokemonUtils.getMove(entity);
                if (move != null) {
                    int originX = (int) (screenWidth * CobblemonFightOrFlight.visualEffectConfig().move_indicator_x_relative);
                    int originY = (int) (screenHeight * CobblemonFightOrFlight.visualEffectConfig().move_indicator_y_relative);
                    Font font = minecraft.font;
                    renderMoveSlot(graphics, font, originX, originY, entity, move);
                }
            }
        }
    }

    public static void renderMoveSlot(GuiGraphics graphics, Font font, int x, int y, PokemonEntity entity, Move move) {
        ElementalType type = move.getType();
        int cooldown = ((PokemonInterface) entity).getAttackTime();
        int maxCooldown = ((PokemonInterface) entity).getMaxAttackTime();
        float cooldownPer = (float) cooldown / maxCooldown;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (cooldown > 2) {
            graphics.setColor(0.5f, 0.5f, 0.5f, 1f);
        }
        graphics.blit(TYPE_ICON_LOCATION, x, y, DRAW_SIZE, DRAW_SIZE, TYPE_ICON_SIZE * type.getTextureXMultiplier(), 0, TYPE_ICON_SIZE, TYPE_ICON_SIZE, TYPE_ICON_SIZE * 18, TYPE_ICON_SIZE);
        graphics.setColor(1f, 1f, 1f, 1f);
        if (cooldown > 1 && maxCooldown != 0) {
            graphics.setColor(0.65f, 0.8f, 1f, 0.95f);
            int cdHeight = (int) ((float) DRAW_SIZE * (1 - cooldownPer));
            graphics.blit(TYPE_ICON_LOCATION, x, y + DRAW_SIZE - cdHeight, DRAW_SIZE, cdHeight, TYPE_ICON_SIZE * type.getTextureXMultiplier(), TYPE_ICON_SIZE - (float) (cdHeight * TYPE_ICON_SIZE) / DRAW_SIZE, TYPE_ICON_SIZE, (int) ((float) (cdHeight * TYPE_ICON_SIZE) / DRAW_SIZE), TYPE_ICON_SIZE * 18, TYPE_ICON_SIZE);
            graphics.setColor(1f, 1f, 1f, 1f);
        }
        RenderSystem.disableBlend();
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(TEXT_SIZE, TEXT_SIZE, 1);
        graphics.drawCenteredString(font, move.getDisplayName(), (int) (x / TEXT_SIZE), (int) (y / TEXT_SIZE), 0xFFFFFF);
        poseStack.popPose();
    }
}