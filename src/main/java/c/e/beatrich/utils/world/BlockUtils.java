package c.e.beatrich.utils.world;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockUtils {

    private static final ThreadLocal<BlockPos.MutableBlockPos> EXPOSED_POS =
            ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    public static boolean isExposed(BlockPos pos) {
        var level = Minecraft.getInstance().level;
        if (level == null) return false;
        for (Direction dir : Direction.values()) {
            if (!level.getBlockState(EXPOSED_POS.get().setWithOffset(pos, dir)).canOcclude())
                return true;
        }
        return false;
    }
}
