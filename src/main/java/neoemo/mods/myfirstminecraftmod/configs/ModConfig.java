package neoemo.mods.myfirstminecraftmod.configs;

import neoemo.mods.myfirstminecraftmod.utils.Tags;

public class ModConfig {
    public String joinMessage = "Привет " + Tags.getPlayer() + ", добро пожаловать в мой мод!";
    public String joinMessage2 = "Ты спросишь меня, что данный мод делает?";
    public String joinMessage3 = "Напиши \"Привет, NeoEmo\" и узнаешь!";
    public String triggerMessage = "Привет, NeoEmo";
    public String replyMessage = "Привет, " + Tags.getPlayer();
}
