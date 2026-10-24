package neoemo.mods.myfirstminecraftmod;

import neoemo.mods.myfirstminecraftmod.configs.ModConfig;
import neoemo.mods.myfirstminecraftmod.managers.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Myfirstminecraftmod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("myfirstminecraftmod");

    @Override
    public void onInitialize() {
        LOGGER.info("Мод успешно загружен");

        ModConfig config = ConfigManager.load();

        ServerPlayConnectionEvents
                .JOIN.register((handler, sender, server) -> {
                    String playerName = handler.getPlayer().getName().getString();
                    /* Во всех случаях на всякий стоит замена имени,
                       в случае если кому-то надо вывести имя игрока в каком-то из них
                     */
                    String msg1 = config.joinMessage.replace("%player%", playerName);
                    String msg2 = config.joinMessage2.replace("%player%", playerName);
                    String msg3 = config.joinMessage3.replace("%player%", playerName);

                    handler.getPlayer().sendMessage(Text.of(msg1), false);
                    handler.getPlayer().sendMessage(Text.of(msg2), false);
                    handler.getPlayer().sendMessage(Text.of(msg3), false);
        });

        ServerMessageEvents
                .CHAT_MESSAGE.register((message, sender, params) -> {
            String messageText = message.getSignedContent();

            if (messageText.equalsIgnoreCase(config.triggerMessage)) {
                String playerName = sender.getName().getString();

                String replyMessage = config.replyMessage.replace("%player%", playerName);
                sender.sendMessage(Text.literal(replyMessage), false);
            }
        });
    }
}
