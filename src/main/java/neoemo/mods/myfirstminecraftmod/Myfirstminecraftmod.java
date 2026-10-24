package neoemo.mods.myfirstminecraftmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Myfirstminecraftmod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("myfirstminecraftmod");

    @Override
    public void onInitialize() {
        LOGGER.info("Мод успешно загружен");

        ServerPlayConnectionEvents
                .JOIN
                .register((handler, sender, server) -> {
            String playerName = handler.getPlayer().getName().getString();

            String msg1 = "Привет " + playerName + ", добро пожаловать в мой мод!";
            String msg2 = "Ты спросишь у меня, что он делает?";
            String msg3 = "Напиши \"Привет, NeoEmo\" и узнаешь!";

            handler.getPlayer().sendMessage(Text.literal(msg1), false);
            handler.getPlayer().sendMessage(Text.literal(msg2), false);
            handler.getPlayer().sendMessage(Text.literal(msg3), false);
        });

        ServerMessageEvents
                .CHAT_MESSAGE
                .register((message, sender, params) -> {
            String messageText = message.getSignedContent();

            if (messageText.equalsIgnoreCase("Привет, Neoemo")) {
                String playerName = sender.getName().getString();

                String replyText = "Привет, " +  playerName + "!";
                sender.sendMessage(Text.literal(replyText), false);
            }
        });
    }
}
