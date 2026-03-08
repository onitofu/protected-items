package ru.nyansus.mc.indestructibleItems;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public final class IndestructibleHelpTopic extends HelpTopic {

    private final IndestructibleItems plugin;

    public IndestructibleHelpTopic(IndestructibleItems plugin) {
        this.plugin = plugin;
        this.name = "/protected";
        this.shortText = plugin.getMessages().get("en", "command.help-short");
    }

    @Override
    public String getFullText(CommandSender forWho) {
        return plugin.getMessages().get(forWho, "command.help-full");
    }

    @Override
    public boolean canSee(CommandSender sender) {
        return sender.hasPermission(Permissions.USE);
    }
}
