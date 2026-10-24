package neoemo.mods.myfirstminecraftmod.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import neoemo.mods.myfirstminecraftmod.Myfirstminecraftmod;
import neoemo.mods.myfirstminecraftmod.configs.ModConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_PATH = FabricLoader
            .getInstance().getConfigDir().resolve("MyFirstMinecraftMod.json");

    public static ModConfig load() {
        ModConfig modConfig = new ModConfig();

        if (!Files.exists(CONFIG_PATH)) {
            save(modConfig);
            return modConfig;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModConfig loadedConfig = GSON.fromJson(reader, ModConfig.class);
            if (loadedConfig != null) {
                return loadedConfig;
            }
        } catch (IOException e) {
            Myfirstminecraftmod.LOGGER.error("Не удалось прочитать конфиг!", e);
        }

        return modConfig;
    }

    private static void save(ModConfig modConfig) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(modConfig, writer);
            }
        } catch (IOException e) {
            Myfirstminecraftmod.LOGGER.error("Не удалось сохранить конфиг!", e);
        }
    }
}
