package ru.nyansus.mc.protected_items.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.protected_items.ProtectedItems;
import ru.nyansus.mc.protected_items.ProtectionUtil;

import java.util.List;

public final class AddCommand extends HeldItemCommand {

    public AddCommand(ProtectedItems plugin) {
        super(plugin);
    }

    @Override
    protected void executeWithItem(Player player, ItemStack hand, String[] args) {
        String loreText = plugin.getMessages().get(player, "item.protected-lore");
        ProtectionUtil.setProtected(hand, true, loreText);
        player.sendMessage(plugin.getMessages().get(player, "command.add-success"));
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-add");
    }
}
