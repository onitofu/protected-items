package ru.nyansus.mc.protected_items;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class InteractionProtectionListener implements Listener {

    private final ProtectedItems plugin;

    public InteractionProtectionListener(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (ProtectionUtil.isProtected(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-drop"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.isProtectionEnabled()) {
            return;
        }
        if (ProtectionUtil.isProtected(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-place"));
        }
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
        if (!ProtectionUtil.isProtected(hand)) {
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
        if (!ProtectionUtil.isProtected(item)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(plugin.getMessages().get(event.getPlayer(), "action.cannot-use"));
    }
}
