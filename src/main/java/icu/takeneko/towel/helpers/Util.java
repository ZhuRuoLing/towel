package icu.takeneko.towel.helpers;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.util.Facing;

public class Util {
    public static int opposite(int i) {
        return Facing.oppositeSide[i];
    }

    public static boolean isOutsideOfWorld(BlockPos pos) {
        return isOutsideOfWorld(pos.x, pos.y, pos.z);
    }

    public static boolean isOutsideOfWorld(int x, int y, int z) {
        return x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256;
    }

    public static double distantSqr(Entity a, Entity b) {
        return a.getDistanceSq(b.posX, b.posY, b.posZ);
    }

    public static Entity getLowestRidingEntity(Entity e) {
        Entity result = e.ridingEntity;
        while (result != null && result.ridingEntity != null) {
            result = result.ridingEntity;
        }
        return result;
    }
}
