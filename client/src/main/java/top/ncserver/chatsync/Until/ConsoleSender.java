package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import top.ncserver.chatsync.Client;
import top.ncserver.chatsync.Chatsync;
import top.ncserver.chatsync.V2.Until.MsgTool;

import java.io.IOException;
import java.util.*;

public class ConsoleSender implements ConsoleCommandSender {
    private static int taskid;
    private final Server server;
    private final ArrayList<String> output = new ArrayList<>();
    public ConsoleSender(Server server) {
        this.server = server;

    }

    private void send() {
        try{
            Bukkit.getScheduler().cancelTask(taskid);
            taskid = Bukkit.getScheduler().runTaskLaterAsynchronously(Chatsync.getPlugin(Chatsync.class), () -> {
                StringBuilder output = new StringBuilder();
                for (String s : this.output) {
                    output.append(s).append("\n");
                }
                Map<String, Object> msg = new HashMap<>();
                msg.put("type", "command");
                msg.put("command", output);
                JSONObject jo = new JSONObject(msg);
                MsgTool.msgSend(Client.session, jo.toJSONString());
                this.output.clear();
            }, 4).getTaskId();
        }catch (Exception e) {
            Map<String, Object> msg = new HashMap<>();
            msg.put("type", "msg");
            msg.put("sender","命令执行");
            msg.put("command", "命令执行时出现错误\n"+e.getMessage());
            JSONObject jo = new JSONObject(msg);
            MsgTool.msgSend(Client.session, jo.toJSONString());
        }

    }


    private Optional<ConsoleCommandSender> get() {
        return Optional.ofNullable(this.server.getConsoleSender());
    }

    @Override
    public Server getServer() {
        return this.server;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        this.output.add(message);
        send();
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }

    @Override
    public boolean isPermissionSet(String s) {
        return get().map(c -> c.isPermissionSet(s)).orElse(true);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return get().map(c -> c.isPermissionSet(permission)).orElse(true);
    }

    @Override
    public boolean hasPermission(String s) {
        return get().map(c -> c.hasPermission(s)).orElse(true);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return get().map(c -> c.hasPermission(permission)).orElse(true);
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean b) {
        throw new UnsupportedOperationException();
    }

    // just throw UnsupportedOperationException - we never use any of these methods
    @Override
    public Spigot spigot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConversing() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void acceptConversationInput(String s) {
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRawMessage(String s) {
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recalculatePermissions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new UnsupportedOperationException();
    }
}
