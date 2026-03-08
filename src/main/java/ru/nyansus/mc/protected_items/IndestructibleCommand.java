package ru.nyansus.mc.protected_items;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nyansus.mc.protected_items.command.AddCommand;
import ru.nyansus.mc.protected_items.command.CheckCommand;
import ru.nyansus.mc.protected_items.command.ICommand;
import ru.nyansus.mc.protected_items.command.ListAllCommand;
import ru.nyansus.mc.protected_items.command.ListCommand;
import ru.nyansus.mc.protected_items.command.RemoveCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class IndestructibleCommand implements CommandExecutor, TabCompleter {

    private final IndestructibleItems plugin;
    private final Map<String, ICommand> subCommands = new HashMap<>();
    private final List<ICommand> orderedSubCommands = new ArrayList<>();

    public IndestructibleCommand(IndestructibleItems plugin) {
        this.plugin = plugin;
        register(new AddCommand(plugin), "add", "on");
        register(new RemoveCommand(plugin), "remove", "off");
        register(new CheckCommand(plugin), "check");
        register(new ListCommand(plugin), "list");
        register(new ListAllCommand(plugin), "listall");
    }

    private void register(ICommand cmd, String... names) {
        orderedSubCommands.add(cmd);
        for (String name : names) {
            subCommands.put(name, cmd);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.USE)) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        ICommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sendUsage(sender);
            return true;
        }

        String perm = sub.requiredPermission();
        if (perm != null && !sender.hasPermission(perm)) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.no-permission"));
            return true;
        }

        sub.execute(sender, args);
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getMessages().get(sender, "command.usage-title"));
        for (ICommand sub : orderedSubCommands) {
            String perm = sub.requiredPermission();
            if (perm != null && !sender.hasPermission(perm)) {
                continue;
            }
            for (String key : sub.getUsageKeys(sender)) {
                sender.sendMessage(plugin.getMessages().get(sender, key));
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(Permissions.USE)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return subCommands.entrySet().stream()
                    .filter(e -> {
                        String perm = e.getValue().requiredPermission();
                        return perm == null || sender.hasPermission(perm);
                    })
                    .map(Map.Entry::getKey)
                    .filter(s -> s.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (args.length >= 2) {
            ICommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null) {
                return sub.tabComplete(sender, args);
            }
        }
        return Collections.emptyList();
    }
}
