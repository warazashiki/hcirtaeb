package c.e.beatrich.module.modules.misc;

import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.setting.types.BlockListSetting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.LinkedHashSet;
import java.util.Set;

public class test extends Module {

    public BlockListSetting oreBlocks = blockListSetting("OreBlocks", "矿物方块", allOreBlocks());

    public test() {
        super("test", "test", Category.MISC, false);
    }

    private static Set<Block> allOreBlocks() {
        return new LinkedHashSet<>();
    }
}
