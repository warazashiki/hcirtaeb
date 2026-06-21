package c.e.beatrich.setting.types;

import c.e.beatrich.setting.Setting;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 方块列表设置 — 存储一组 {@link Block}，供 ESP/XRay 等模块使用。
 */
public class BlockListSetting extends Setting<Set<Block>> {

    public BlockListSetting(String name, String description, Set<Block> defaultValue) {
        super(name, description, new LinkedHashSet<>(defaultValue));
    }

    public boolean contains(Block block) {
        return value.contains(block);
    }

    public void add(Block block) {
        value.add(block);
        fireChange();
    }

    public void remove(Block block) {
        value.remove(block);
        fireChange();
    }

    public void clear() {
        value.clear();
        fireChange();
    }

    @Override
    public void set(Set<Block> value) {
        this.value = new LinkedHashSet<>(value);
        fireChange();
    }
}
