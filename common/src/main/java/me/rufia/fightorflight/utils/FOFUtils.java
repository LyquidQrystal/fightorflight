package me.rufia.fightorflight.utils;

import me.rufia.fightorflight.CobblemonFightOrFlight;
import me.rufia.fightorflight.data.movedata.MoveData;
import me.rufia.fightorflight.item.component.PokeStaffComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FOFUtils {
    public static Vec3i stringToVec3i(String data) {
        if (data.startsWith("VEC3i_")) {
            Pattern p = Pattern.compile("VEC3i_([-\\d]*)_([-\\d]*)_([-\\d]*)");//I know it's not safe, but who will send other data?
            Matcher m = p.matcher(data);
            if (m.find()) {
                try {
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int z = Integer.parseInt(m.group(3));
                    return new Vec3i(x, y, z);
                    //CobblemonFightOrFlight.LOGGER.info("Generated position:x: %d y: %d z: %d".formatted(x, y, z));
                } catch (NumberFormatException e) {
                    CobblemonFightOrFlight.LOGGER.warn("Failed to converse the vec");
                }
            }
        }
        return null;
    }

    public static String createCommandData(Player player, PokeStaffComponent.CMDMODE cmdmode) {
        String cmdData = "";
        switch (cmdmode) {
            case MOVE, ATTACK_POSITION, MOVE_ATTACK -> {
                BlockHitResult result = RayTrace.rayTraceBlock(player, 16);
                BlockPos blockPos = result.getBlockPos();
                //CobblemonFightOrFlight.LOGGER.info("VEC3_%s_%s_%s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                cmdData = "VEC3i_%s_%s_%s".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
            case ATTACK -> {
                LivingEntity livingEntity = RayTrace.rayTraceEntity(player, 16);
                if (livingEntity != null) {
                    cmdData = "ENTITY_%s".formatted(livingEntity.getStringUUID());
                    //CobblemonFightOrFlight.LOGGER.info("ENTITY_%s".formatted(livingEntity.getStringUUID()));
                }
            }
            case STAY -> cmdData = "POS_SELF";
            default -> cmdData = "";
        }
        return cmdData;
    }

    public static boolean chanceTest(List<Boolean> conditions, List<Float> chances, RandomSource source) {
        if (conditions.size() != chances.size()) {
            CobblemonFightOrFlight.LOGGER.info("The size of the two arrays are not the same");
            return false;
        }
        float chance = source.nextFloat();
        for (int i = 0; i < conditions.size(); ++i) {
            if (conditions.get(i)) {
                return chances.get(i) > chance;
            }
        }
        return false;
    }

    public static float toAngle(double num) {
        return (float) (num * 57.2957763671875);
    }

    public static float toRad(double num) {
        return (float) (num / 180 * 3.1415927f);
    }

    public static void registerMoveData(String moveName, MoveData data) {
        if (MoveData.moveData.containsKey(moveName)) {
            if (MoveData.moveData.get(moveName) != null) {
                MoveData.moveData.get(moveName).add(data);
            }
        } else {
            MoveData.moveData.put(moveName, new ArrayList<>());
            MoveData.moveData.get(moveName).add(data);
        }
    }

    public static boolean teamCheck(LivingEntity entity1, LivingEntity entity2) {
        if (entity1 instanceof TamableAnimal animal1 && animal1.getOwner() != null) {
            if (entity2 instanceof TamableAnimal animal2 && animal2.getOwner() != null) {
                LivingEntity owner1 = animal1.getOwner();
                LivingEntity owner2 = animal2.getOwner();
                if (owner1.getTeam() != null || owner2.getTeam() != null) {
                    return owner1.getTeam() == owner2.getTeam() || owner1.getTeam() == animal2.getTeam() && owner1.getTeam() != null || animal1.getTeam() == owner2.getTeam() && owner2.getTeam() != null;
                }
            }
        }
        if (entity1.getTeam() != null || entity2.getTeam() != null) {
            return entity1.getTeam() == entity2.getTeam();
        }
        return false;
    }

}
