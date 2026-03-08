package ru.nyansus.mc.protected_items.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    void execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    /**
     * Extra permission required beyond {@code protecteditems.use}.
     * Return {@code null} if no extra permission is needed.
     */
    default @Nullable String requiredPermission() {
        return null;
    }

    List<String> getUsageKeys(CommandSender sender);
}
