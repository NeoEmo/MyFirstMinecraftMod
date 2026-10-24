package neoemo.mods.myfirstminecraftmod;

import neoemo.mods.myfirstminecraftmod.configs.ModConfig;
import neoemo.mods.myfirstminecraftmod.managers.ConfigManager;
import neoemo.mods.myfirstminecraftmod.utils.Tags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Myfirstminecraftmod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("myfirstminecraftmod");

    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {
        LOGGER.info("Мод успешно загружен");

        CONFIG = ConfigManager.load();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("myFirstMinecraftMod")
                    .then(CommandManager.literal("reload")
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> {
                                CONFIG = ConfigManager.load();
                                context.getSource().sendFeedback(() -> Text.literal("§aКонфиг MyFirstMinecraftMod успешно перезагружен!"), false);
                                return 1;
                            })
                    )
            );
        });

        ServerPlayConnectionEvents
                .JOIN.register((handler, sender, server) -> {
                    String playerName = handler.getPlayer().getName().getString();
                    /* Во всех случаях на всякий стоит замена имени,
                       в случае если кому-то надо вывести имя игрока в каком-то из них
                     */
                    String msg1 = CONFIG.joinMessage.replace(Tags.getPlayer(), playerName);
                    String msg2 = CONFIG.joinMessage2.replace(Tags.getPlayer(), playerName);
                    String msg3 = CONFIG.joinMessage3.replace(Tags.getPlayer(), playerName);

                    handler.getPlayer().sendMessage(Text.of(msg1), false);
                    handler.getPlayer().sendMessage(Text.of(msg2), false);
                    handler.getPlayer().sendMessage(Text.of(msg3), false);
        });

        ServerMessageEvents
                .CHAT_MESSAGE.register((message, sender, params) -> {
            String messageText = message.getSignedContent();

            if (messageText.equalsIgnoreCase(CONFIG.triggerMessage)) {
                String playerName = sender.getName().getString();

                String replyMessage = CONFIG.replyMessage.replace(Tags.getPlayer(), playerName);
                sender.sendMessage(Text.literal(replyMessage), false);
            }
        });
    }
}
