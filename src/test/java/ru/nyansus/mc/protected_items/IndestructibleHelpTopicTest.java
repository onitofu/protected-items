package ru.nyansus.mc.protected_items;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IndestructibleHelpTopicTest {

    private ServerMock server;
    private IndestructibleItems plugin;
    private IndestructibleHelpTopic helpTopic;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(IndestructibleItems.class);
        helpTopic = new IndestructibleHelpTopic(plugin);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void canSee_withPermission_returnsTrue() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", true);

        assertTrue(helpTopic.canSee(player));
    }

    @Test
    public void canSee_withoutPermission_returnsFalse() {
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "protecteditems.use", false);

        assertFalse(helpTopic.canSee(player));
    }

    @Test
    public void getFullText_returnsNonNull() {
        PlayerMock player = server.addPlayer();

        String text = helpTopic.getFullText(player);

        assertNotNull(text);
        assertTrue(text.contains("/protected"));
    }
}
