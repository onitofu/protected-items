package ru.nyansus.mc.indestructibleItems;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IndestructibleCommandTest {

    private ServerMock server;
    private IndestructibleItems plugin;
    private IndestructibleCommand command;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(IndestructibleItems.class);
        command = new IndestructibleCommand(plugin);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void onCommand_withoutPermission_sendsNoPermission() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", false);

        boolean result = command.onCommand(player, null, "protected", new String[]{"add"});

        assertTrue(result);
        player.assertSaid(plugin.getMessages().get(player, "command.no-permission"));
    }

    @Test
    public void onCommand_withPermission_emptyHand_sendsHoldItem() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        boolean result = command.onCommand(player, null, "protected", new String[]{"add"});

        assertTrue(result);
        player.assertSaid(plugin.getMessages().get(player, "command.hold-item"));
    }

    @Test
    public void onCommand_add_withItem_marksAndSendsSuccess() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        player.getInventory().setItemInMainHand(sword);

        boolean result = command.onCommand(player, null, "protected", new String[]{"add"});

        assertTrue(result);
        assertTrue(IndestructibleUtil.isIndestructible(player.getInventory().getItemInMainHand()));
        player.assertSaid(plugin.getMessages().get(player, "command.add-success"));
    }

    @Test
    public void onCommand_remove_clearsFlag() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        player.getInventory().setItemInMainHand(sword);

        command.onCommand(player, null, "protected", new String[]{"remove"});

        assertFalse(IndestructibleUtil.isIndestructible(player.getInventory().getItemInMainHand()));
    }

    @Test
    public void onCommand_check_indestructibleItem_returnsIndestructibleMessage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        player.getInventory().setItemInMainHand(sword);

        command.onCommand(player, null, "protected", new String[]{"check"});

        player.assertSaid(plugin.getMessages().get(player, "command.check-indestructible"));
    }

    @Test
    public void onCommand_check_normalItem_returnsNormalMessage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        command.onCommand(player, null, "protected", new String[]{"check"});

        player.assertSaid(plugin.getMessages().get(player, "command.check-normal"));
    }

    @Test
    public void onTabComplete_withoutPermission_returnsEmpty() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", false);

        java.util.List<String> list = command.onTabComplete(player, null, "protected", new String[]{"ad"});

        assertTrue(list == null || list.isEmpty());
    }

    @Test
    public void onTabComplete_withPermission_returnsMatchingSubcommands() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        java.util.List<String> list = command.onTabComplete(player, null, "protected", new String[]{"ad"});

        assertTrue(list != null && list.contains("add"));
    }
}
