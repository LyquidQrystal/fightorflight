package me.rufia.fightorflight;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;


public interface PokemonInterface {
    default int getAttackTime() {
        return 0;
    }


    default void setAttackTime(int val) {
    }

    default int getMaxAttackTime() {
        return 0;
    }


    default void setMaxAttackTime(int val) {
    }

    default boolean usingBeam() {
        return false;
    }


    default boolean usingSound() {
        return false;
    }

    default boolean usingMagic() {
        return false;
    }

    default void setCurrentMove(Move move) {
    }


    default String getCurrentMove() {
        return null;
    }


    default int getNextCryTime() {
        return 0;
    }

    default void setNextCryTime(int time) {
    }

    default String getCommand() {
        return "";
    }

    default void setCommand(String cmd) {
    }

    default String getCommandData() {
        return "";
    }

    default void setCommandData(String cmdData) {
    }

    default BlockPos getTargetBlockPos() {
        return null;
    }

    default void setTargetBlockPos(BlockPos blockPos) {
    }

    default int getCapturedBy() {
        return 0;
    }

    default void setCapturedBy(int id) {

    }

    default void refreshMovesList() {
    }

    default void switchMove(Move move) {}

    default void tryUsingStatusMoves(){}

    default int getAttackMode() {
        return 0;
    }

    default void setAttackMode(int attackMode){}
}
