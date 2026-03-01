package ru.nyansus.mc.indestructibleItems;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class IndestructibleListeners implements Listener {

    private final IndestructibleItems plugin;
    private final ConcurrentHashMap<UUID, List<ItemStack>> keptOnDeath = new ConcurrentHashMap<>();

    public IndestructibleListeners(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (IndestructibleUtil.isIndestructible(plugin, event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-drop"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> toKeep = new ArrayList<>();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());

        for (ItemStack drop : drops) {
            if (IndestructibleUtil.isIndestructible(plugin, drop)) {
                toKeep.add(drop.clone());
            }
        }

        if (toKeep.isEmpty()) {
            return;
        }

        drops.removeIf(stack -> IndestructibleUtil.isIndestructible(plugin, stack));
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (IndestructibleUtil.isIndestructible(plugin, event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-place"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission("indestructibleitems.bypass-store")) {
            return;
        }
        InventoryView view = event.getView();
        if (view.getTopInventory().getType() == org.bukkit.event.inventory.InventoryType.PLAYER) {
            return;
        }
        int topSize = view.getTopInventory().getSize();
        boolean cancel = false;
        if (IndestructibleUtil.isIndestructible(plugin, event.getCursor())) {
            if (event.getRawSlot() < topSize) {
                cancel = true;
            }
        }
        if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            if (event.getRawSlot() >= topSize && IndestructibleUtil.isIndestructible(plugin, event.getCurrentItem())) {
                cancel = true;
            }
        }
        if (cancel) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessages().get(player, "action.cannot-store"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission("indestructibleitems.bypass-store")) {
            return;
        }
        if (event.getView().getTopInventory().getType() == org.bukkit.event.inventory.InventoryType.PLAYER) {
            return;
        }
        if (!IndestructibleUtil.isIndestructible(plugin, event.getOldCursor())) {
            return;
        }
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessages().get(player, "action.cannot-store"));
                return;
            }
        }
    }
}
