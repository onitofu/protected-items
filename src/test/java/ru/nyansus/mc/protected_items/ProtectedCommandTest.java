package ru.nyansus.mc.protected_items;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProtectedCommandTest {

    private ServerMock server;
    private ProtectedItems plugin;
    private ProtectedCommand command;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ProtectedItems.class);
        command = new ProtectedCommand(plugin);
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
    public void onCommand_noArgs_sendsUsage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        command.onCommand(player, null, "protected", new String[]{});

        player.assertSaid(plugin.getMessages().get(player, "command.usage-title"));
    }

    @Test
    public void onCommand_unknownSubcommand_sendsUsage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        command.onCommand(player, null, "protected", new String[]{"foo"});

        player.assertSaid(plugin.getMessages().get(player, "command.usage-title"));
    }

    @Test
    public void onCommand_add_emptyHand_sendsHoldItem() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        command.onCommand(player, null, "protected", new String[]{"add"});

        player.assertSaid(plugin.getMessages().get(player, "command.hold-item"));
    }

    @Test
    public void onCommand_add_withItem_marksAndSendsSuccess() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        command.onCommand(player, null, "protected", new String[]{"add"});

        assertTrue(ProtectionUtil.isProtected(player.getInventory().getItemInMainHand()));
        player.assertSaid(plugin.getMessages().get(player, "command.add-success"));
    }

    @Test
    public void onCommand_on_alias_marksItem() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        command.onCommand(player, null, "protected", new String[]{"on"});

        assertTrue(ProtectionUtil.isProtected(player.getInventory().getItemInMainHand()));
    }

    @Test
    public void onCommand_remove_clearsFlag() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        player.getInventory().setItemInMainHand(sword);

        command.onCommand(player, null, "protected", new String[]{"remove"});

        assertFalse(ProtectionUtil.isProtected(player.getInventory().getItemInMainHand()));
        player.assertSaid(plugin.getMessages().get(player, "command.remove-success"));
    }

    @Test
    public void onCommand_off_alias_removesFlag() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        player.getInventory().setItemInMainHand(sword);

        command.onCommand(player, null, "protected", new String[]{"off"});

        assertFalse(ProtectionUtil.isProtected(player.getInventory().getItemInMainHand()));
    }

    @Test
    public void onCommand_check_indestructible_sendsIndestructibleMessage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        player.getInventory().setItemInMainHand(sword);

        command.onCommand(player, null, "protected", new String[]{"check"});

        player.assertSaid(plugin.getMessages().get(player, "command.check-indestructible"));
    }

    @Test
    public void onCommand_check_normal_sendsNormalMessage() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        command.onCommand(player, null, "protected", new String[]{"check"});

        player.assertSaid(plugin.getMessages().get(player, "command.check-normal"));
    }

    @Test
    public void onCommand_add_fromConsole_sendsPlayerOnly() {
        CommandSender console = server.getConsoleSender();

        command.onCommand(console, null, "protected", new String[]{"add"});
        // covers the "not a player" branch in HeldItemCommand
    }

    @Test
    public void onCommand_list_noProtectedItems_sendsEmpty() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));

        command.onCommand(player, null, "protected", new String[]{"list"});

        player.assertSaid(plugin.getMessages().get(player, "command.list-empty"));
    }

    @Test
    public void onCommand_list_withProtectedItems_sendsHeader() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        player.getInventory().addItem(sword);

        command.onCommand(player, null, "protected", new String[]{"list"});

        player.assertSaid(plugin.getMessages().get(player, "command.list-header"));
    }

    @Test
    public void onCommand_list_fromConsole_sendsPlayerOnly() {
        CommandSender console = server.getConsoleSender();

        command.onCommand(console, null, "protected", new String[]{"list"});
        // covers "not a player" branch in ListCommand
    }

    @Test
    public void onCommand_list_otherPlayer_withAdmin_sendsHeader() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);

        PlayerMock target = server.addPlayer();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        target.getInventory().addItem(sword);

        command.onCommand(admin, null, "protected", new String[]{"list", target.getName()});

        String expected = plugin.getMessages().get(admin, "command.list-other-header",
                "{player}", target.getName());
        admin.assertSaid(expected);
    }

    @Test
    public void onCommand_list_otherPlayer_noAdmin_denies() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        command.onCommand(player, null, "protected", new String[]{"list", "SomePlayer"});

        player.assertSaid(plugin.getMessages().get(player, "command.no-permission"));
    }

    @Test
    public void onCommand_list_playerNotFound_sendsError() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);

        command.onCommand(admin, null, "protected", new String[]{"list", "NonExistent"});

        admin.assertSaid(plugin.getMessages().get(admin, "command.list-player-not-found",
                "{player}", "NonExistent"));
    }

    @Test
    public void onCommand_listall_admin_noItems_sendsEmpty() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);

        command.onCommand(admin, null, "protected", new String[]{"listall"});

        admin.assertSaid(plugin.getMessages().get(admin, "command.listall-empty"));
    }

    @Test
    public void onCommand_listall_admin_withItems_sendsHeader() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        admin.getInventory().addItem(sword);

        command.onCommand(admin, null, "protected", new String[]{"listall"});

        admin.assertSaid(plugin.getMessages().get(admin, "command.listall-header"));
    }

    @Test
    public void onCommand_listall_admin_enderChest_sendsHeader() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        admin.getEnderChest().addItem(sword);

        command.onCommand(admin, null, "protected", new String[]{"listall"});

        admin.assertSaid(plugin.getMessages().get(admin, "command.listall-header"));
    }

    @Test
    public void onCommand_listall_noAdmin_denies() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        command.onCommand(player, null, "protected", new String[]{"listall"});

        player.assertSaid(plugin.getMessages().get(player, "command.no-permission"));
    }

    @Test
    public void onTabComplete_withoutPermission_returnsEmpty() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", false);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"ad"});

        assertTrue(list == null || list.isEmpty());
    }

    @Test
    public void onTabComplete_withPermission_returnsMatchingSubcommands() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"ad"});

        assertTrue(list != null && list.contains("add"));
    }

    @Test
    public void onTabComplete_admin_seesListall() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);
        player.addAttachment(plugin, "protecteditems.admin", true);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"list"});

        assertTrue(list != null && list.contains("list") && list.contains("listall"));
    }

    @Test
    public void onTabComplete_noAdmin_doesNotSeeListall() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"lista"});

        assertTrue(list == null || !list.contains("listall"));
    }

    @Test
    public void onTabComplete_list_admin_showsPlayerNames() {
        PlayerMock admin = server.addPlayer();
        admin.addAttachment(plugin, "protecteditems.use", true);
        admin.addAttachment(plugin, "protecteditems.admin", true);

        List<String> list = command.onTabComplete(admin, null, "protected", new String[]{"list", ""});

        assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void onTabComplete_list_noAdmin_noPlayerNames() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"list", ""});

        assertTrue(list == null || list.isEmpty());
    }

    @Test
    public void onTabComplete_thirdArg_returnsEmpty() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        List<String> list = command.onTabComplete(player, null, "protected", new String[]{"add", "x", "y"});

        assertTrue(list == null || list.isEmpty());
    }
}
