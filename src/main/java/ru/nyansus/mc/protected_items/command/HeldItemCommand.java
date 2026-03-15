package ru.nyansus.mc.protected_items.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.nyansus.mc.protected_items.ProtectedItems;

/**
 * Base for subcommands that require a player holding a non-air item.
 */
public abstract class HeldItemCommand implements ICommand {

    protected final ProtectedItems plugin;

    protected HeldItemCommand(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public final void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.player-only"));
            return;
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            player.sendMessage(plugin.getMessages().get(player, "command.hold-item"));
            return;
        }
        executeWithItem(player, hand, args);
    }

    protected abstract void executeWithItem(Player player, ItemStack hand, String[] args);
}
