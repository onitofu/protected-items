package ru.nyansus.mc.indestructibleItems;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Тесты утилиты пометки предметов как неуничтожаемых (PDC).
 */
public class IndestructibleUtilTest {

    private ServerMock server;
    private IndestructibleItems plugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(IndestructibleItems.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void isIndestructible_emptyItem_returnsFalse() {
        ItemStack air = new ItemStack(Material.AIR);
        assertFalse(IndestructibleUtil.isIndestructible(plugin, air));
    }

    @Test
    public void isIndestructible_null_returnsFalse() {
        assertFalse(IndestructibleUtil.isIndestructible(plugin, null));
    }

    @Test
    public void isIndestructible_normalItem_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        assertFalse(IndestructibleUtil.isIndestructible(plugin, sword));
    }

    @Test
    public void setIndestructible_add_thenIsIndestructible_returnsTrue() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(plugin, sword, true);
        assertTrue(IndestructibleUtil.isIndestructible(plugin, sword));
    }

    @Test
    public void setIndestructible_remove_thenIsIndestructible_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(plugin, sword, true);
        IndestructibleUtil.setIndestructible(plugin, sword, false);
        assertFalse(IndestructibleUtil.isIndestructible(plugin, sword));
    }

    @Test
    public void getKey_returnsSameKeyForSamePlugin() {
        var key1 = IndestructibleUtil.getKey(plugin);
        var key2 = IndestructibleUtil.getKey(plugin);
        assertEquals(key1.getNamespace(), key2.getNamespace());
        assertEquals(key1.getKey(), key2.getKey());
    }
}
