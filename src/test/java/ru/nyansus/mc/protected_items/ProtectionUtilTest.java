package ru.nyansus.mc.protected_items;

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

public class ProtectionUtilTest {

    private ServerMock server;
    private ProtectedItems plugin;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ProtectedItems.class);
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void isProtected_emptyItem_returnsFalse() {
        ItemStack air = new ItemStack(Material.AIR);
        assertFalse(ProtectionUtil.isProtected(air));
    }

    @Test
    public void isProtected_null_returnsFalse() {
        assertFalse(ProtectionUtil.isProtected(null));
    }

    @Test
    public void isProtected_normalItem_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        assertFalse(ProtectionUtil.isProtected(sword));
    }

    @Test
    public void setProtected_add_thenIsProtected_returnsTrue() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        assertTrue(ProtectionUtil.isProtected(sword));
    }

    @Test
    public void setProtected_remove_thenIsProtected_returnsFalse() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ProtectionUtil.setProtected(sword, true);
        ProtectionUtil.setProtected(sword, false);
        assertFalse(ProtectionUtil.isProtected(sword));
    }

    @Test
    public void getKey_returnsCachedInstance() {
        assertNotNull(ProtectionUtil.getKey());
        assertSame(ProtectionUtil.getKey(), ProtectionUtil.getKey());
    }

    @Test
    public void setProtected_null_doesNotThrow() {
        ProtectionUtil.setProtected(null, true);
        ProtectionUtil.setProtected(null, false);
    }

    @Test
    public void formatItemName_normalItem_capitalizedWords() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        String name = ProtectionUtil.formatItemName(sword);
        assertEquals("Diamond Sword", name);
    }

    @Test
    public void formatItemName_singleWord_capitalized() {
        ItemStack dirt = new ItemStack(Material.DIRT);
        String name = ProtectionUtil.formatItemName(dirt);
        assertEquals("Dirt", name);
    }
}
