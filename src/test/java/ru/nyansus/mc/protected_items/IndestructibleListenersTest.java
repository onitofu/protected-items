package ru.nyansus.mc.protected_items;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IndestructibleListenersTest {

    private ServerMock server;
    private IndestructibleItems plugin;
    private IndestructibleListeners listeners;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(IndestructibleItems.class);
        listeners = new IndestructibleListeners(plugin);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    // --- Drop ---

    @Test
    public void onDrop_protectedItem_cancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Location loc = new Location(world, 0, 64, 0);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        Item droppedItem = world.dropItem(loc, sword);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        listeners.onDrop(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onDrop_normalItem_notCancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Location loc = new Location(world, 0, 64, 0);

        Item droppedItem = world.dropItem(loc, new ItemStack(Material.DIAMOND_SWORD));

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        listeners.onDrop(event);

        assertFalse(event.isCancelled());
    }

    // --- Quit ---

    @Test
    public void onQuit_cleansUpKeptItems() {
        PlayerMock player = server.addPlayer();
        PlayerQuitEvent event = new PlayerQuitEvent(player, (String) null);
        listeners.onQuit(event);
        // no exception = success; entry removed if it existed
    }

    // --- Block Place ---

    @Test
    public void onBlockPlace_protectedItem_cancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Block block = world.getBlockAt(0, 64, 0);
        block.setType(Material.DIRT);

        ItemStack item = new ItemStack(Material.DIRT);
        IndestructibleUtil.setIndestructible(item, true);

        BlockPlaceEvent event = new BlockPlaceEvent(
                block, block.getState(), block, item, player, true, EquipmentSlot.HAND);
        listeners.onBlockPlace(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onBlockPlace_normalItem_notCancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Block block = world.getBlockAt(0, 64, 0);
        block.setType(Material.DIRT);

        ItemStack item = new ItemStack(Material.DIRT);

        BlockPlaceEvent event = new BlockPlaceEvent(
                block, block.getState(), block, item, player, true, EquipmentSlot.HAND);
        listeners.onBlockPlace(event);

        assertFalse(event.isCancelled());
    }

    // --- Right Click Use ---

    @Test
    public void onRightClickUse_protectedItem_rightClickAir_cancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        player.getInventory().setItemInMainHand(sword);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        listeners.onRightClickUse(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onRightClickUse_protectedItem_rightClickBlock_cancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_BLOCK, sword, null, null, EquipmentSlot.HAND);
        listeners.onRightClickUse(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onRightClickUse_normalItem_notCancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        boolean initial = event.isCancelled();
        listeners.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }

    @Test
    public void onRightClickUse_leftClick_ignored() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.LEFT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        boolean initial = event.isCancelled();
        listeners.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }

    @Test
    public void onRightClickUse_offHand_ignored() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.OFF_HAND);
        boolean initial = event.isCancelled();
        listeners.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }

    // --- Inventory Click ---

    @Test
    public void onInventoryClick_withBypass_notCancelled() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.bypass-store", true);

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listeners.onInventoryClick(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryClick_protectedCursor_toChest_cancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        view.setCursor(sword);

        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listeners.onInventoryClick(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onInventoryClick_normalCursor_toChest_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        view.setCursor(new ItemStack(Material.DIAMOND_SWORD));

        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listeners.onInventoryClick(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryClick_cursorToBottomSlot_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        view.setCursor(sword);

        int playerSlot = view.getTopInventory().getSize() + 5;
        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, playerSlot, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listeners.onInventoryClick(event);

        assertFalse(event.isCancelled());
    }

    // --- Inventory Drag ---

    @Test
    public void onInventoryDrag_withBypass_notCancelled() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.bypass-store", true);

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(0, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listeners.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryDrag_protectedItem_toChest_cancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(0, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listeners.onInventoryDrag(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onInventoryDrag_normalItem_toChest_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(0, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listeners.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryDrag_protectedItem_toBottomOnly_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);

        int bottomSlot = view.getTopInventory().getSize() + 5;
        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(bottomSlot, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listeners.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }
}
