package ru.nyansus.mc.protected_items.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.protected_items.ProtectedItems;
import ru.nyansus.mc.protected_items.ProtectionUtil;
import ru.nyansus.mc.protected_items.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ListCommand implements ICommand {

    private final ProtectedItems plugin;

    public ListCommand(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (!sender.hasPermission(Permissions.ADMIN)) {
                sender.sendMessage(plugin.getMessages().get(sender, "command.no-permission"));
                return;
            }
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessages().get(sender, "command.list-player-not-found",
                        "{player}", targetName));
                return;
            }
            listPlayerItems(sender, target, true);
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(plugin.getMessages().get(sender, "command.player-only"));
                return;
            }
            listPlayerItems(sender, player, false);
        }
    }

    private void listPlayerItems(CommandSender sender, Player target, boolean other) {
        List<ItemStack> found = new ArrayList<>();
        for (ItemStack item : target.getInventory().getContents()) {
            if (ProtectionUtil.isProtected(item)) {
                found.add(item);
            }
        }

        if (found.isEmpty()) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.list-empty"));
            return;
        }

        String header = other
                ? plugin.getMessages().get(sender, "command.list-other-header", "{player}", target.getName())
                : plugin.getMessages().get(sender, "command.list-header");
        sender.sendMessage(header);

        for (ItemStack item : found) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.list-item",
                    "{item}", ProtectionUtil.formatItemName(item),
                    "{amount}", String.valueOf(item.getAmount())));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && sender.hasPermission(Permissions.ADMIN)) {
            String prefix = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        List<String> keys = new ArrayList<>();
        keys.add("command.usage-list");
        if (sender.hasPermission(Permissions.ADMIN)) {
            keys.add("command.usage-list-other");
        }
        return keys;
    }
}
