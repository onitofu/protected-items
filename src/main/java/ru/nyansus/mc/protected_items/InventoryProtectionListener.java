package ru.nyansus.mc.protected_items;

import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class InventoryProtectionListener implements Listener {

    private final ProtectedItems plugin;

    public InventoryProtectionListener(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        BlockState state = event.getBlock().getState();
        if (!(state instanceof Container container)) {
            return;
        }
        Player player = event.getPlayer();
        List<ItemStack> protectedItems = new ArrayList<>();
        for (ItemStack item : container.getInventory().getContents()) {
            if (ProtectionUtil.isProtected(item)) {
                protectedItems.add(item.clone());
            }
        }
        if (protectedItems.isEmpty()) {
            return;
        }
        container.getInventory().removeItem(protectedItems.toArray(new ItemStack[0]));
        for (ItemStack item : protectedItems) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                displaceRandomItem(player, item);
            }
        }
    }

    private void displaceRandomItem(Player player, ItemStack protectedItem) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        List<Integer> nonProtectedSlots = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && !contents[i].getType().isAir()
                    && !ProtectionUtil.isProtected(contents[i])) {
                nonProtectedSlots.add(i);
            }
        }
        if (nonProtectedSlots.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), protectedItem);
            return;
        }
        int slot = nonProtectedSlots.get(ThreadLocalRandom.current().nextInt(nonProtectedSlots.size()));
        ItemStack displaced = contents[slot].clone();
        player.getInventory().setItem(slot, protectedItem);
        player.getWorld().dropItemNaturally(player.getLocation(), displaced);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission(Permissions.BYPASS_STORE)) {
            return;
        }
        InventoryView view = event.getView();
        if (!isExternalContainer(view)) {
            return;
        }
        int topSize = view.getTopInventory().getSize();
        boolean cancel = false;
        if (ProtectionUtil.isProtected(event.getCursor())) {
            if (event.getRawSlot() < topSize) {
                cancel = true;
            }
        }
        if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            if (event.getRawSlot() >= topSize && ProtectionUtil.isProtected(event.getCurrentItem())) {
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
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission(Permissions.BYPASS_STORE)) {
            return;
        }
        if (!isExternalContainer(event.getView())) {
            return;
        }
        if (!ProtectionUtil.isProtected(event.getOldCursor())) {
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

    private static boolean isExternalContainer(InventoryView view) {
        InventoryType type = view.getTopInventory().getType();
        return type != InventoryType.PLAYER && type != InventoryType.ENDER_CHEST;
    }
}
