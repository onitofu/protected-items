package ru.nyansus.mc.protected_items.command;

import org.bukkit.command.CommandSender;
import ru.nyansus.mc.protected_items.IndestructibleItems;
import ru.nyansus.mc.protected_items.Permissions;

import java.util.List;

public final class ToggleCommand implements ICommand {

    private final IndestructibleItems plugin;

    public ToggleCommand(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean newState = !plugin.isProtectionEnabled();
        plugin.setProtectionEnabled(newState);
        String key = newState ? "command.toggle-enabled" : "command.toggle-disabled";
        sender.sendMessage(plugin.getMessages().get(sender, key));
    }

    @Override
    public String requiredPermission() {
        return Permissions.ADMIN;
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-toggle");
    }
}
