package ru.nyansus.mc.protected_items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ProtectionUtil {

    private static final String KEY = "indestructible";
    private static final String ORIGINAL_NAME_KEY = "original-name";
    private static final String LORE_MARKER_KEY = "lore-marker";
    private static final String LORE_MARKER_SYMBOL = "\uD83D\uDD12 ";

    private static final TextColor GRADIENT_START = TextColor.color(0xDA, 0xB0, 0xFF);
    private static final TextColor GRADIENT_END = TextColor.color(0x8A, 0x2B, 0xE2);
    private static final TextColor LORE_COLOR = TextColor.color(0xBB, 0x86, 0xFC);

    private static final Set<Material> DISABLED_MATERIALS = ConcurrentHashMap.newKeySet();

    private static NamespacedKey cachedKey;
    private static NamespacedKey originalNameKey;
    private static NamespacedKey loreMarkerKey;

    private ProtectionUtil() {
    }

    public static void init(JavaPlugin plugin) {
        cachedKey = new NamespacedKey(plugin, KEY);
        originalNameKey = new NamespacedKey(plugin, ORIGINAL_NAME_KEY);
        loreMarkerKey = new NamespacedKey(plugin, LORE_MARKER_KEY);
    }

    public static NamespacedKey getKey() {
        return cachedKey;
    }

    public static boolean isProtected(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        if (DISABLED_MATERIALS.contains(item.getType())) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().get(cachedKey, PersistentDataType.BYTE) != null;
    }

    public static boolean toggleMaterial(Material material) {
        if (DISABLED_MATERIALS.remove(material)) {
            return true;
        }
        DISABLED_MATERIALS.add(material);
        return false;
    }

    public static boolean isMaterialEnabled(Material material) {
        return !DISABLED_MATERIALS.contains(material);
    }

    public static Set<Material> getDisabledMaterials() {
        return Collections.unmodifiableSet(DISABLED_MATERIALS);
    }

    public static void setProtected(ItemStack item, boolean value, String loreText) {
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (value) {
            applyProtection(item, meta, loreText);
        } else {
            removeProtection(item, meta);
        }
    }

    public static void setProtected(ItemStack item, boolean value) {
        setProtected(item, value, null);
    }

    private static void applyProtection(ItemStack item, ItemMeta meta, String loreText) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(cachedKey, PersistentDataType.BYTE, (byte) 1);

        Component originalName = meta.displayName();
        String serialized = originalName != null
                ? GsonComponentSerializer.gson().serialize(originalName)
                : "";
        pdc.set(originalNameKey, PersistentDataType.STRING, serialized);

        if (originalName != null) {
            String plainName = PlainTextComponentSerializer.plainText().serialize(originalName);
            meta.displayName(createGoldGradient(plainName));
        } else {
            meta.displayName(createTranslatableName(item));
        }

        String marker = loreText != null ? LORE_MARKER_SYMBOL + loreText : LORE_MARKER_SYMBOL + "Protected";
        pdc.set(loreMarkerKey, PersistentDataType.STRING, marker);

        List<Component> lore = meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(Component.text(marker, LORE_COLOR).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        item.setItemMeta(meta);
    }

    private static void removeProtection(ItemStack item, ItemMeta meta) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(cachedKey);

        String serialized = pdc.get(originalNameKey, PersistentDataType.STRING);
        if (serialized != null) {
            if (serialized.isEmpty()) {
                meta.displayName(null);
            } else {
                meta.displayName(GsonComponentSerializer.gson().deserialize(serialized));
            }
            pdc.remove(originalNameKey);
        }

        String marker = pdc.get(loreMarkerKey, PersistentDataType.STRING);
        pdc.remove(loreMarkerKey);

        List<Component> lore = meta.lore();
        if (lore != null) {
            String matchText = marker != null ? marker : LORE_MARKER_SYMBOL;
            lore.removeIf(line -> PlainTextComponentSerializer.plainText()
                    .serialize(line).contains(matchText));
            meta.lore(lore.isEmpty() ? null : lore);
        }

        item.setItemMeta(meta);
    }

    private static Component createTranslatableName(ItemStack item) {
        return Component.translatable(item.getType().translationKey())
                .color(GRADIENT_END)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static Component createGoldGradient(String text) {
        if (text.isEmpty()) {
            return Component.empty();
        }
        TextComponent.Builder builder = Component.text();
        for (int i = 0; i < text.length(); i++) {
            float ratio = text.length() > 1 ? (float) i / (text.length() - 1) : 0;
            int r = (int) (GRADIENT_START.red() + ratio * (GRADIENT_END.red() - GRADIENT_START.red()));
            int g = (int) (GRADIENT_START.green() + ratio * (GRADIENT_END.green() - GRADIENT_START.green()));
            int b = (int) (GRADIENT_START.blue() + ratio * (GRADIENT_END.blue() - GRADIENT_START.blue()));
            builder.append(Component.text(text.charAt(i), TextColor.color(r, g, b))
                    .decoration(TextDecoration.ITALIC, false));
        }
        return builder.build();
    }

    public static String formatItemName(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            String stored = meta.getPersistentDataContainer().get(originalNameKey, PersistentDataType.STRING);
            if (stored != null) {
                if (!stored.isEmpty()) {
                    Component original = GsonComponentSerializer.gson().deserialize(stored);
                    return PlainTextComponentSerializer.plainText().serialize(original);
                }
                return formatRawName(item);
            }
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return formatRawName(item);
    }

    private static String formatRawName(ItemStack item) {
        String name = item.getType().name().toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (!sb.isEmpty()) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1));
        }
        return sb.toString();
    }
}
