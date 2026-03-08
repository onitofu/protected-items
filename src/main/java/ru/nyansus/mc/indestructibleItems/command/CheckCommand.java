package ru.nyansus.mc.indestructibleItems.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.indestructibleItems.IndestructibleItems;
import ru.nyansus.mc.indestructibleItems.IndestructibleUtil;

import java.util.List;

public final class CheckCommand extends HeldItemCommand {

    public CheckCommand(IndestructibleItems plugin) {
        super(plugin);
    }

    @Override
    protected void executeWithItem(Player player, ItemStack hand, String[] args) {
        boolean indestructible = IndestructibleUtil.isIndestructible(hand);
        String key = indestructible ? "command.check-indestructible" : "command.check-normal";
        player.sendMessage(plugin.getMessages().get(player, key));
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-check");
    }
}
