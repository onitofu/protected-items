package ru.nyansus.mc.protected_items;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InventoryProtectionListenerTest {

    private ServerMock server;
    private ProtectedItems plugin;
    private InventoryProtectionListener listener;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ProtectedItems.class);
        listener = new InventoryProtectionListener(plugin);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
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
        listener.onInventoryClick(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryClick_protectedCursor_toChest_cancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        view.setCursor(sword);

        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listener.onInventoryClick(event);

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
        listener.onInventoryClick(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryClick_cursorToBottomSlot_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        view.setCursor(sword);

        int playerSlot = view.getTopInventory().getSize() + 5;
        InventoryClickEvent event = new InventoryClickEvent(
                view, InventoryType.SlotType.CONTAINER, playerSlot, ClickType.LEFT, InventoryAction.PLACE_ALL);
        listener.onInventoryClick(event);

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
        ProtectionUtil.setProtected(sword, true);

        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(0, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listener.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryDrag_protectedItem_toChest_cancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);

        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(0, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listener.onInventoryDrag(event);

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
        listener.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void onInventoryDrag_protectedItem_toBottomOnly_notCancelled() {
        PlayerMock player = server.addPlayer();

        Inventory chest = server.createInventory(null, InventoryType.CHEST);
        player.openInventory(chest);
        InventoryView view = player.getOpenInventory();

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);

        int bottomSlot = view.getTopInventory().getSize() + 5;
        Map<Integer, ItemStack> slots = new HashMap<>();
        slots.put(bottomSlot, sword);

        InventoryDragEvent event = new InventoryDragEvent(view, null, sword, false, slots);
        listener.onInventoryDrag(event);

        assertFalse(event.isCancelled());
    }
}
