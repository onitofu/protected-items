package ru.nyansus.mc.indestructibleItems;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class IndestructibleUtil {

    private static final String KEY = "indestructible";

    private IndestructibleUtil() {
    }

    public static NamespacedKey getKey(JavaPlugin plugin) {
        return new NamespacedKey(plugin, KEY);
    }

    public static boolean isIndestructible(JavaPlugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().get(getKey(plugin), PersistentDataType.BYTE) != null;
    }

    public static void setIndestructible(JavaPlugin plugin, ItemStack item, boolean value) {
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        if (value) {
            meta.getPersistentDataContainer().set(getKey(plugin), PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(getKey(plugin));
        }
        item.setItemMeta(meta);
    }
}
