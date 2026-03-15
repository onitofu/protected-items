package ru.nyansus.mc.protected_items;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DeathProtectionListener implements Listener {

    private final ProtectedItems plugin;
    private final ConcurrentHashMap<UUID, List<ItemStack>> keptOnDeath = new ConcurrentHashMap<>();

    public DeathProtectionListener(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        Player player = event.getEntity();
        List<ItemStack> toKeep = new ArrayList<>();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());

        for (ItemStack drop : drops) {
            if (ProtectionUtil.isProtected(drop)) {
                toKeep.add(drop.clone());
            }
        }

        if (toKeep.isEmpty()) {
            return;
        }

        drops.removeIf(ProtectionUtil::isProtected);
        event.getDrops().clear();
        event.getDrops().addAll(drops);
        keptOnDeath.put(player.getUniqueId(), toKeep);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> restored = keptOnDeath.remove(player.getUniqueId());
        if (restored == null || restored.isEmpty()) {
            return;
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (ItemStack item : restored) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        keptOnDeath.remove(event.getPlayer().getUniqueId());
    }
}
