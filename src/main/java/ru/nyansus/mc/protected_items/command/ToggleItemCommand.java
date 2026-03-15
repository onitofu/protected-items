package ru.nyansus.mc.protected_items.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import ru.nyansus.mc.protected_items.Permissions;
import ru.nyansus.mc.protected_items.ProtectedItems;
import ru.nyansus.mc.protected_items.ProtectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class ToggleItemCommand implements ICommand {

    private final ProtectedItems plugin;

    public ToggleItemCommand(ProtectedItems plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.toggleitem-usage"));
            return;
        }
        String input = args[1].toLowerCase(Locale.ROOT).replace("minecraft:", "");
        Material material = Material.matchMaterial(input);
        if (material == null) {
            sender.sendMessage(plugin.getMessages().get(sender, "command.toggleitem-unknown",
                    "{item}", args[1]));
            return;
        }
        boolean enabled = ProtectionUtil.toggleMaterial(material);
        String key = enabled ? "command.toggleitem-enabled" : "command.toggleitem-disabled";
        String name = material.name().toLowerCase(Locale.ROOT);
        sender.sendMessage(plugin.getMessages().get(sender, key, "{item}", name));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            return Arrays.stream(Material.values())
                    .filter(Material::isItem)
                    .map(m -> m.name().toLowerCase(Locale.ROOT))
                    .filter(n -> n.startsWith(prefix))
                    .limit(30)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public String requiredPermission() {
        return Permissions.ADMIN;
    }

    @Override
    public List<String> getUsageKeys(CommandSender sender) {
        return List.of("command.usage-toggleitem");
    }
}
