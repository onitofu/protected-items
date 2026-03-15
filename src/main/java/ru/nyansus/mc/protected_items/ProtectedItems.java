package ru.nyansus.mc.protected_items;

import org.bukkit.plugin.java.JavaPlugin;

public class ProtectedItems extends JavaPlugin {

    private Messages messages;
    private boolean protectionEnabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ProtectionUtil.init(this);
        messages = new Messages(this);
        ProtectedCommand cmd = new ProtectedCommand(this);
        getCommand("protected").setExecutor(cmd);
        getCommand("protected").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new DeathProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractionProtectionListener(this), this);
        getServer().getHelpMap().addTopic(new ProtectedHelpTopic(this));
    }

    public Messages getMessages() {
        return messages;
    }

    public boolean isProtectionEnabled() {
        return protectionEnabled;
    }

    public void setProtectionEnabled(boolean enabled) {
        this.protectionEnabled = enabled;
    }

    @Override
    public void onDisable() {
    }
}
