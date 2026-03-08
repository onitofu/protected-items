package ru.nyansus.mc.indestructibleItems.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.indestructibleItems.IndestructibleItems;
import ru.nyansus.mc.indestructibleItems.IndestructibleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ListCommand implements ICommand {

    private final IndestructibleItems plugin;

    public ListCommand(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (!sender.hasPermission("protecteditems.admin")) {
                sender.sendMessage(plugin.getMessages().get(sender, "command.no-permission"));
                return;
            }
            String targetName = args[1];
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(plugin.getMessages().get(sender, "command.list-player-not-found")
                        .replace("{player}", targetName));
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
            if (IndestructibleUtil.isIndestructible(plugin, item)) {
                found.add(item);
            }
        }

        if (found.isEmpty()) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.list-empty"));
            return;
        }

        String header = other
                ? plugin.getMessages().get(sender, "command.list-other-header").replace("{player}", target.getName())
                : plugin.getMessages().get(sender, "command.list-header");
        sender.sendMessage(header);

        for (ItemStack item : found) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.list-item")
                    .replace("{item}", IndestructibleUtil.formatItemName(item))
                    .replace("{amount}", String.valueOf(item.getAmount())));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2 && sender.hasPermission("protecteditems.admin")) {
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
        if (sender.hasPermission("protecteditems.admin")) {
            keys.add("command.usage-list-other");
        }
        return keys;
    }
}
