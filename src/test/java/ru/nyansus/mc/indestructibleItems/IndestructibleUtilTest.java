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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
        assertFalse(IndestructibleUtil.isIndestructible(air));
    }

    @Test
    public void isIndestructible_null_returnsFalse() {
        assertFalse(IndestructibleUtil.isIndestructible(null));
    }

    @Test
    public void isIndestructible_normalItem_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        assertFalse(IndestructibleUtil.isIndestructible(sword));
    }

    @Test
    public void setIndestructible_add_thenIsIndestructible_returnsTrue() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        assertTrue(IndestructibleUtil.isIndestructible(sword));
    }

    @Test
    public void setIndestructible_remove_thenIsIndestructible_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        IndestructibleUtil.setIndestructible(sword, true);
        IndestructibleUtil.setIndestructible(sword, false);
        assertFalse(IndestructibleUtil.isIndestructible(sword));
    }

    @Test
    public void getKey_returnsCachedInstance() {
        assertNotNull(IndestructibleUtil.getKey());
        assertSame(IndestructibleUtil.getKey(), IndestructibleUtil.getKey());
    }

    @Test
    public void setIndestructible_null_doesNotThrow() {
        IndestructibleUtil.setIndestructible(null, true);
        IndestructibleUtil.setIndestructible(null, false);
    }

    @Test
    public void formatItemName_normalItem_capitalizedWords() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        String name = IndestructibleUtil.formatItemName(sword);
        assertEquals("Diamond Sword", name);
    }

    @Test
    public void formatItemName_singleWord_capitalized() {
        ItemStack dirt = new ItemStack(Material.DIRT);
        String name = IndestructibleUtil.formatItemName(dirt);
        assertEquals("Dirt", name);
    }
}
