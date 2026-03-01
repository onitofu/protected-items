package ru.nyansus.mc.indestructibleItems;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Загрузка и выдача сообщений по локали (ru / en).
 * Для игрока используется локаль клиента, для консоли — из config.yml.
 */
public final class Messages {

    private static final String FALLBACK_LOCALE = "en";
    private static final Map<String, String> LOCALE_MAP = Map.of(
            "ru", "ru",
            "en", "en",
            "ru_ru", "ru",
            "en_us", "en",
            "en_gb", "en"
    );

    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> locales = new HashMap<>();
    private String defaultLocale = FALLBACK_LOCALE;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        locales.clear();
        loadLocale("ru", "messages_ru.yml");
        loadLocale("en", "messages_en.yml");
        plugin.reloadConfig();
        defaultLocale = plugin.getConfig().getString("default-locale", FALLBACK_LOCALE);
        if (!locales.containsKey(defaultLocale)) {
            defaultLocale = FALLBACK_LOCALE;
        }
    }

    private void loadLocale(String locale, String resource) {
        try (InputStream stream = plugin.getResource(resource)) {
            if (stream == null) {
                return;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            locales.put(locale, config);
        } catch (IOException ignored) {
        }
    }

    /**
     * Возвращает сообщение для отправителя команды (игрок — по его локали, консоль — default-locale).
     */
    public String get(CommandSender sender, String key) {
        String locale = defaultLocale;
        if (sender instanceof Player player) {
            locale = normalizeLocale(player.getLocale());
        }
        return get(locale, key);
    }

    /**
     * Возвращает сообщение для игрока по его локали.
     */
    public String get(Player player, String key) {
        return get(normalizeLocale(player.getLocale()), key);
    }

    /**
     * Возвращает сообщение по коду локали (ru, en).
     */
    public String get(String locale, String key) {
        String msg = getFromLocale(locale, key);
        if (msg != null) {
            return msg;
        }
        if (!FALLBACK_LOCALE.equals(locale)) {
            msg = getFromLocale(FALLBACK_LOCALE, key);
        }
        return msg != null ? msg : "[" + key + "]";
    }

    private String getFromLocale(String locale, String key) {
        YamlConfiguration config = locales.get(locale);
        if (config == null) {
            return null;
        }
        return config.getString(key);
    }

    private static String normalizeLocale(String clientLocale) {
        if (clientLocale == null || clientLocale.isEmpty()) {
            return FALLBACK_LOCALE;
        }
        String lower = clientLocale.toLowerCase(Locale.ROOT);
        return LOCALE_MAP.getOrDefault(lower, lower.split("_")[0]);
    }

    public void reload() {
        load();
    }
}
