package ru.nyansus.mc.indestructibleItems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class IndestructibleCommand implements CommandExecutor, TabCompleter {

    private final IndestructibleItems plugin;

    public IndestructibleCommand(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.player-only"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        ItemStack hand = player.getInventory().getItemInMainHand();

        switch (sub) {
            case "add", "on" -> {
                if (hand.getType().isAir()) {
                    player.sendMessage(plugin.getMessages().get(player, "command.hold-item"));
                    return true;
                }
                IndestructibleUtil.setIndestructible(plugin, hand, true);
                player.sendMessage(plugin.getMessages().get(player, "command.add-success"));
                return true;
            }
            case "remove", "off" -> {
                if (hand.getType().isAir()) {
                    player.sendMessage(plugin.getMessages().get(player, "command.hold-item"));
                    return true;
                }
                IndestructibleUtil.setIndestructible(plugin, hand, false);
                player.sendMessage(plugin.getMessages().get(player, "command.remove-success"));
                return true;
            }
            case "check" -> {
                if (hand.getType().isAir()) {
                    player.sendMessage(plugin.getMessages().get(player, "command.hold-item"));
                    return true;
                }
                boolean indestructible = IndestructibleUtil.isIndestructible(plugin, hand);
                String key = indestructible ? "command.check-indestructible" : "command.check-normal";
                player.sendMessage(plugin.getMessages().get(player, key));
                return true;
            }
            default -> {
                sendUsage(sender);
                return true;
            }
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getMessages().get(sender, "command.usage-title"));
        sender.sendMessage(plugin.getMessages().get(sender, "command.usage-add"));
        sender.sendMessage(plugin.getMessages().get(sender, "command.usage-remove"));
        sender.sendMessage(plugin.getMessages().get(sender, "command.usage-check"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        String prefix = args[0].toLowerCase();
        return Stream.of("add", "remove", "check")
                .filter(s -> s.startsWith(prefix))
                .collect(Collectors.toList());
    }
}
