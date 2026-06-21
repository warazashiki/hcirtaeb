package c.e.beatrich.module.modules.render;

import c.e.beatrich.event.events.GetShadeBrightnessEvent;
import c.e.beatrich.event.events.RenderBlockEntityEvent;
import c.e.beatrich.event.events.VisGraphEvent;
import c.e.beatrich.module.Category;
import c.e.beatrich.module.Module;
import c.e.beatrich.module.ModuleManager;
import c.e.beatrich.setting.types.BlockListSetting;
import c.e.beatrich.setting.types.BoolSetting;
import c.e.beatrich.setting.types.IntSetting;
import c.e.beatrich.utils.world.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.LinkedHashSet;
import java.util.Set;

public class XRay extends Module {

    public static final BlockListSetting BList = new BlockListSetting("BlockList", "BlockList", ore());
    public static final BoolSetting ExposedOnly = new BoolSetting("ExposedOnly", "仅透视裸露方块", false);
    public IntSetting CoolDown = intSetting("CoolDown", "刷新间隔", 10, 1, 100);

    private int bListHash;
    private boolean OldEO;
    private int t;

    public XRay() {
        super("XRay", "矿透", Category.RENDER);
        addSetting(BList);
        addSetting(ExposedOnly);
    }
    private static Set<Block> ore() {
        return new LinkedHashSet<>();
    }

    public static boolean isBlockOutList(BlockState state, BlockPos pos) {
        return !BList.contains(state.getBlock()) || (ExposedOnly.get() && (pos == null || !BlockUtils.isExposed(pos)));
    }
    public static boolean RenderFace(BlockState state, BlockGetter level, Direction face, BlockPos pos, boolean returns) {
        if (!returns && !isBlockOutList(state, pos)) {
            BlockPos adjPos = pos.relative(face);
            BlockState adjState = level.getBlockState(adjPos);
            return isBlockOutList(adjState, adjPos);
        }
        return returns;
    }
    public static boolean enabled() {
        return ModuleManager.get().getByName("XRay")
                .map(Module::isActive).orElse(false);
    }
    @Override
    public void onTick(ClientTickEvent.Post event) {
        if (t <= 0) {
            int h = BList.get().hashCode();
            boolean e = ExposedOnly.get();
            if (h != bListHash || e != OldEO) {
                bListHash = h;
                OldEO = e;
                if (isActive()) mc.levelRenderer.allChanged();
            }
            t = CoolDown.get();
        } else {
            t--;
        }
    }
    @Override
    public void onActivate() {
        t = CoolDown.get();
        bListHash = BList.get().hashCode();
        if (mc.levelRenderer != null && mc.player != null) {
            mc.levelRenderer.allChanged();
        }
    }
    @Override
    public void onDeactivate() {
        mc.levelRenderer.allChanged();
    }
    @Override
    public void onRenderBlockEntity(RenderBlockEntityEvent event) {
        if (isBlockOutList(event.blockEntity.getBlockState(), event.blockEntity.getBlockPos())) event.cancel();
    }
    @Override
    public void onVisGraph(VisGraphEvent event) { event.cancel(); }
    @Override
    public void onGetShadeBrightness(GetShadeBrightnessEvent event) {
        event.lightLevel = 1;
    }
}
