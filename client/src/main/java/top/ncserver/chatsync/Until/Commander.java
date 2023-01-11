package top.ncserver.chatsync.Until;


import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Commander implements CommandSender{
    public List<String> message = new ArrayList<>();


    public List<String> getMessage() {
        return this.message;
    }

    public void sendMessage(String message) {
        this.message.add(message);
    }

    public void sendMessage(String[] messages) {
        this.message.addAll(Arrays.asList(messages));
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public String getName() {
        return "QQ_Group";
    }

    public CommandSender.Spigot spigot() {
        return new CommandSender.Spigot() {
            public void sendMessage(BaseComponent component) {
                Commander.this.message.add(component.toLegacyText());
            }

            public void sendMessage(BaseComponent... components) {
                for (BaseComponent temp : components)
                    Commander.this.message.add(temp.toLegacyText());
            }
        };
    }

    public boolean isPermissionSet(String name) {
        return true;
    }

    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    public boolean hasPermission(String name) {
        return true;
    }

    public boolean hasPermission(Permission perm) {
        return true;
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return Bukkit.getConsoleSender().addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return Bukkit.getConsoleSender().addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return Bukkit.getConsoleSender().addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return Bukkit.getConsoleSender().addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {}

    public void recalculatePermissions() {}

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Bukkit.getConsoleSender().getEffectivePermissions();
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {}


}
