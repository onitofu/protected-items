package ru.nyansus.mc.protected_items;

import org.bukkit.plugin.java.JavaPlugin;

public class IndestructibleItems extends JavaPlugin {

    private Messages messages;
    private boolean protectionEnabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        IndestructibleUtil.init(this);
        messages = new Messages(this);
        IndestructibleCommand cmd = new IndestructibleCommand(this);
        getCommand("protected").setExecutor(cmd);
        getCommand("protected").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new IndestructibleListeners(this), this);
        getServer().getHelpMap().addTopic(new IndestructibleHelpTopic(this));
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
