package ru.nyansus.mc.indestructibleItems;

import org.bukkit.plugin.java.JavaPlugin;

public class IndestructibleItems extends JavaPlugin {

    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages = new Messages(this);
        IndestructibleCommand cmd = new IndestructibleCommand(this);
        getCommand("indestructible").setExecutor(cmd);
        getCommand("indestructible").setTabCompleter(cmd);
        getServer().getPluginManager().registerEvents(new IndestructibleListeners(this), this);
        getServer().getHelpMap().addTopic(new IndestructibleHelpTopic(this));
    }

    public Messages getMessages() {
        return messages;
    }

    @Override
    public void onDisable() {
    }
}
