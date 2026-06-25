package c.e.beatrich.module.modules.render;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.types.BlockListSetting;
import c.e.beatrich.setting.types.BoolSetting;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;

public class BlockEsp extends Module {

    public static final BlockListSetting BList = new BlockListSetting("BlockList", "方块列表", blocks());
    public BoolSetting nf = boolSetting("NotFinished", "未完成", false);

    public BlockEsp() {
        super("BlockEsp", "方块透视", Category.RENDER);
        addSetting(BList);
    }

    private static Set<Block> blocks() {
        return new LinkedHashSet<>();
    }
}
