package ru.nyansus.mc.protected_items;

import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class IndestructibleListeners implements Listener {

    private final IndestructibleItems plugin;
    private final ConcurrentHashMap<UUID, List<ItemStack>> keptOnDeath = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public IndestructibleListeners(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (IndestructibleUtil.isIndestructible(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-drop"));
        }
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
            if (IndestructibleUtil.isIndestructible(drop)) {
                toKeep.add(drop.clone());
            }
        }

        if (toKeep.isEmpty()) {
            return;
        }

        drops.removeIf(IndestructibleUtil::isIndestructible);
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (IndestructibleUtil.isIndestructible(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-place"));
        }
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
            if (IndestructibleUtil.isIndestructible(item)) {
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
                    && !IndestructibleUtil.isIndestructible(contents[i])) {
                nonProtectedSlots.add(i);
            }
        }
        if (nonProtectedSlots.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), protectedItem);
            return;
        }
        int slot = nonProtectedSlots.get(random.nextInt(nonProtectedSlots.size()));
        ItemStack displaced = contents[slot].clone();
        player.getInventory().setItem(slot, protectedItem);
        player.getWorld().dropItemNaturally(player.getLocation(), displaced);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!IndestructibleUtil.isIndestructible(hand)) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage(plugin.getMessages().get(player, "action.cannot-frame"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRightClickUse(PlayerInteractEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (!IndestructibleUtil.isIndestructible(item)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-use"));
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
        if (IndestructibleUtil.isIndestructible(event.getCursor())) {
            if (event.getRawSlot() < topSize) {
                cancel = true;
            }
        }
        if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
            if (event.getRawSlot() >= topSize && IndestructibleUtil.isIndestructible(event.getCurrentItem())) {
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
        if (!IndestructibleUtil.isIndestructible(event.getOldCursor())) {
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
