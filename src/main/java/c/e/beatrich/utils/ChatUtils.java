package c.e.beatrich.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static void sendMsg(String message) {
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(message), false);
        }
    }
}
