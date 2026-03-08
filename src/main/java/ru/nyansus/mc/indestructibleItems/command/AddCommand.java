package ru.nyansus.mc.indestructibleItems.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.indestructibleItems.IndestructibleItems;
import ru.nyansus.mc.indestructibleItems.IndestructibleUtil;

import java.util.List;

public final class AddCommand extends HeldItemCommand {

    public AddCommand(IndestructibleItems plugin) {
        super(plugin);
    }

    @Override
    protected void executeWithItem(Player player, ItemStack hand, String[] args) {
        IndestructibleUtil.setIndestructible(hand, true);
        player.sendMessage(plugin.getMessages().get(player, "command.add-success"));
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-add");
    }
}
