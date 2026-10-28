package neoemo.mods.myfirstminecraftmod;

import neoemo.mods.myfirstminecraftmod.configs.ModConfig;
import neoemo.mods.myfirstminecraftmod.managers.ConfigManager;
import neoemo.mods.myfirstminecraftmod.utils.Cards.Card;
import neoemo.mods.myfirstminecraftmod.utils.Cards.Deck;
import neoemo.mods.myfirstminecraftmod.utils.Cards.Suit;
import neoemo.mods.myfirstminecraftmod.utils.Random;
import neoemo.mods.myfirstminecraftmod.utils.Tags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Myfirstminecraftmod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("myfirstminecraftmod");

    public static ModConfig CONFIG;
    private static final Map<UUID, Long> lastCardDrawTime = new HashMap<>();
    private static final Long COOLDOWN_TICKS = 24000L;

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
            String playerName = sender.getName().getString();
            String randomCheck = messageText.trim().toLowerCase();
            String randomTrigger = CONFIG.random.toLowerCase();


            if (messageText.equalsIgnoreCase(CONFIG.triggerMessage)) {
                String replyMessage = CONFIG.replyMessage.replace(Tags.getPlayer(), playerName);
                sender.sendMessage(Text.literal(replyMessage), false);
            }

            if (randomCheck.startsWith(randomTrigger)) {
                try {
                    int max = Integer.parseInt(randomCheck.substring(randomTrigger.length()).trim());
                    if (max > 0) {
                        int result = Random.random(max);
                        Text actionMessage = Text.literal("* " + playerName + " выпало число " + result + " из " + max)
                                .formatted(Formatting.ITALIC);

                        sender.getServer().getPlayerManager().broadcast(actionMessage, false);
                    } else {
                        sender.sendMessage(Text.literal("§cЧисло должно быть больше нуля!"), false);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Text.literal("§cОшибка! Напиши число. Пример: " + CONFIG.random + " 10"), false);
                }
            }

            if(messageText.equalsIgnoreCase(CONFIG.cardTrigger)) {
                UUID playerID = sender.getUuid();
                long currentTime = sender.getServerWorld().getTime();

                if (lastCardDrawTime.containsKey(playerID)) {
                    long lastDrawTime = lastCardDrawTime.get(playerID);
                    long timePassed = currentTime - lastDrawTime;

                    if (timePassed < COOLDOWN_TICKS) {
                        long timeLeft = COOLDOWN_TICKS - timePassed;
                        long minutes = timeLeft / 1200;
                        long seconds = (timeLeft % 1200) / 20;

                        sender.sendMessage(Text.literal("§cТы недавно уже тянул карту! Подожди еще " + minutes +
                                " мин. " + seconds + " сек."), false);
                        return;
                    }
                }

                Deck deck = new Deck();
                Card card = deck.getDeck().poll();

                Suit trumpSuit = Suit.values()[Random.random(Suit.values().length) - 1];
                String suitName = Deck.getSuitName(card.getSuit());
                String rankName = card.getRank().toString();

                Text cardMessage = Text.literal("§e" + playerName + " тянет карту: §l" + rankName + " " + suitName + "§r");
                Text trumpMessage = Text.literal("§6Козырь на этот ход: " + Deck.getSuitName(trumpSuit));

                if (card.getSuit() == trumpSuit) {
                    cardMessage = cardMessage.copy().append(" §a(Это козырь!)");
                }

                sender.getServer().getPlayerManager().broadcast(cardMessage, false);
                sender.getServer().getPlayerManager().broadcast(trumpMessage, false);

                lastCardDrawTime.put(playerID, currentTime);
            }
        });
    }
}
