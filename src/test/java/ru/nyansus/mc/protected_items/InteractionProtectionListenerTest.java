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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InteractionProtectionListenerTest {

    private ServerMock server;
    private ProtectedItems plugin;
    private InteractionProtectionListener listener;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ProtectedItems.class);
        listener = new InteractionProtectionListener(plugin);
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
        ProtectionUtil.setProtected(sword, true);
        Item droppedItem = world.dropItem(loc, sword);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        listener.onDrop(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onDrop_normalItem_notCancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Location loc = new Location(world, 0, 64, 0);

        Item droppedItem = world.dropItem(loc, new ItemStack(Material.DIAMOND_SWORD));

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, droppedItem);
        listener.onDrop(event);

        assertFalse(event.isCancelled());
    }

    // --- Block Place ---

    @Test
    public void onBlockPlace_protectedItem_cancelled() {
        PlayerMock player = server.addPlayer();
        WorldMock world = server.addSimpleWorld("test");
        Block block = world.getBlockAt(0, 64, 0);
        block.setType(Material.DIRT);

        ItemStack item = new ItemStack(Material.DIRT);
        ProtectionUtil.setProtected(item, true);

        BlockPlaceEvent event = new BlockPlaceEvent(
                block, block.getState(), block, item, player, true, EquipmentSlot.HAND);
        listener.onBlockPlace(event);

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
        listener.onBlockPlace(event);

        assertFalse(event.isCancelled());
    }

    // --- Right Click Use ---

    @Test
    public void onRightClickUse_protectedItem_rightClickAir_cancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        player.getInventory().setItemInMainHand(sword);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        listener.onRightClickUse(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onRightClickUse_protectedItem_rightClickBlock_cancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_BLOCK, sword, null, null, EquipmentSlot.HAND);
        listener.onRightClickUse(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void onRightClickUse_normalItem_notCancelled() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        boolean initial = event.isCancelled();
        listener.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }

    @Test
    public void onRightClickUse_leftClick_ignored() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.LEFT_CLICK_AIR, sword, null, null, EquipmentSlot.HAND);
        boolean initial = event.isCancelled();
        listener.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }

    @Test
    public void onRightClickUse_offHand_ignored() {
        PlayerMock player = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_AIR, sword, null, null, EquipmentSlot.OFF_HAND);
        boolean initial = event.isCancelled();
        listener.onRightClickUse(event);

        assertEquals(initial, event.isCancelled());
    }
}
