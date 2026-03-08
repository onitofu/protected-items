package ru.nyansus.mc.indestructibleItems;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MessagesTest {

    private ServerMock server;
    private IndestructibleItems plugin;
    private Messages messages;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(IndestructibleItems.class);
        messages = plugin.getMessages();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void get_ru_returnsRussianMessage() {
        String msg = messages.get("ru", "command.hold-item");
        assertNotNull(msg);
        assertTrue(msg.contains("руку") || msg.contains("предмет"));
    }

    @Test
    public void get_en_returnsEnglishMessage() {
        String msg = messages.get("en", "command.hold-item");
        assertNotNull(msg);
        assertTrue(msg.contains("hand") || msg.contains("item"));
    }

    @Test
    public void get_unknownKey_returnsKeyInBrackets() {
        String msg = messages.get("en", "unknown.key");
        assertEquals("[unknown.key]", msg);
    }

    @Test
    public void get_unknownLocale_fallbackToEnglish() {
        String msg = messages.get("xx", "command.hold-item");
        assertNotNull(msg);
        assertTrue(msg.contains("hand") || msg.contains("item") || msg.contains("Hold"));
    }

    @Test
    public void get_helpFull_containsCommandSubcommands() {
        String ru = messages.get("ru", "command.help-full");
        String en = messages.get("en", "command.help-full");
        assertNotNull(ru);
        assertNotNull(en);
        assertTrue(ru.contains("/protected"));
        assertTrue(en.contains("/protected"));
    }
}
