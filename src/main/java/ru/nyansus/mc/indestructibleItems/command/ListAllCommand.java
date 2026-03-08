package ru.nyansus.mc.indestructibleItems.command;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.indestructibleItems.IndestructibleItems;
import ru.nyansus.mc.indestructibleItems.IndestructibleUtil;
import ru.nyansus.mc.indestructibleItems.Permissions;

import java.util.ArrayList;
import java.util.List;

public final class ListAllCommand implements ICommand {

    private final IndestructibleItems plugin;

    public ListAllCommand(IndestructibleItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPermission() {
        return Permissions.ADMIN;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        List<String> lines = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            collectFromInventory(sender, player, lines);
            collectFromEnderChest(sender, player, lines);
        }

        for (World world : Bukkit.getWorlds()) {
            collectFromContainers(sender, world, lines);
        }

        if (lines.isEmpty()) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.listall-empty"));
            return;
        }

        sender.sendMessage(plugin.getMessages().get(sender, "command.listall-header"));
        for (String line : lines) {
            sender.sendMessage(line);
        }
    }

    private void collectFromInventory(CommandSender sender, Player player, List<String> lines) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (IndestructibleUtil.isIndestructible(item)) {
                lines.add(plugin.getMessages().get(sender, "command.listall-inventory",
                        "{item}", IndestructibleUtil.formatItemName(item),
                        "{amount}", String.valueOf(item.getAmount()),
                        "{player}", player.getName()));
            }
        }
    }

    private void collectFromEnderChest(CommandSender sender, Player player, List<String> lines) {
        for (ItemStack item : player.getEnderChest().getContents()) {
            if (IndestructibleUtil.isIndestructible(item)) {
                lines.add(plugin.getMessages().get(sender, "command.listall-enderchest",
                        "{item}", IndestructibleUtil.formatItemName(item),
                        "{amount}", String.valueOf(item.getAmount()),
                        "{player}", player.getName()));
            }
        }
    }

    private void collectFromContainers(CommandSender sender, World world, List<String> lines) {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (!(state instanceof Container container)) {
                    continue;
                }
                for (ItemStack item : container.getInventory().getContents()) {
                    if (IndestructibleUtil.isIndestructible(item)) {
                        Location loc = state.getLocation();
                        lines.add(plugin.getMessages().get(sender, "command.listall-container",
                                "{item}", IndestructibleUtil.formatItemName(item),
                                "{amount}", String.valueOf(item.getAmount()),
                                "{world}", world.getName(),
                                "{x}", String.valueOf(loc.getBlockX()),
                                "{y}", String.valueOf(loc.getBlockY()),
                                "{z}", String.valueOf(loc.getBlockZ())));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-listall");
    }
}
