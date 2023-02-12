package top.ncserver.chatsync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import top.ncserver.chatsync.Until.Metrics;
import top.ncserver.chatsync.V2.Until.MsgTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Chatsync extends JavaPlugin implements Listener {
    public Logger logger = this.getLogger();

    Client c;
    Map<String, Object> msg = new HashMap<>();

    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] arrayOfByte = new byte[63];
            int i;
            while ((i = inputStream.read(arrayOfByte)) > 0) {
                fileOutputStream.write(arrayOfByte, 0, i);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {

        getServer().getMessenger().registerOutgoingPluginChannel(this, channel);
        File configFile = new File(Chatsync.getPlugin(Chatsync.class).getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copyFile(Chatsync.getPlugin(Chatsync.class).getResource("config.yml"), configFile);

            Chatsync.getPlugin(Chatsync.class).getLogger().info("File: 已生成 config.yml 文件");
        }
        Metrics metrics = new Metrics(this, 17411);
        Bukkit.getPluginCommand("qqmsg").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        logger.info("聊天同步插件已加载");
        logger.info("尝试连接");
        System.out.println("连接丢失,重新连接");
        Object[] players = this.getServer().getOnlinePlayers().toArray();
        for (Object player : players) {
            ((Player) player).getPlayer().sendMessage("§a[消息同步]消息同步插件加载成功,当前版本:" + getPlugin(this.getClass()).getDescription().getVersion());
        }
        c = new Client();
        c.runTaskAsynchronously(this);

    }

    @Override
    public void onDisable() {
        c.cancel();
        try {
            Client.session.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("chatsync.qqmsg")) {
            commandSender.sendMessage("§c您没有这个命令的权限");
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage("§c这个命令需要参数！");
            return true;
        }
        logger.info("[发送消息]" + strings[0]);
        msg.clear();
        msg.put("type", "obRe");
        msg.put("msg", strings[0]);
        JSONObject jo = new JSONObject(msg);
        MsgTool.msgSend(Client.session, jo.toJSONString());

        return true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        msg.clear();
        msg.put("type", "msg");
        msg.put("sender", event.getPlayer().getDisplayName());
        msg.put("msg", event.getMessage());
        JSONObject jo = new JSONObject(msg);
        MsgTool.msgSend(Client.session, jo.toJSONString());

    }
    private final String channel = "chatimg:img";
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        msg.clear();
        msg.put("type", "playerJoinAndQuit");
        msg.put("player", event.getPlayer().getDisplayName());
        msg.put("msg", "加入了服务器");
        JSONObject jo = new JSONObject(msg);
        MsgTool.msgSend(Client.session, jo.toJSONString());

        Player player = event.getPlayer();
        try {
            Class<? extends CommandSender> senderClass = player.getClass();
            Method addChannel = senderClass.getDeclaredMethod("addChannel", String.class);
            addChannel.setAccessible(true);
            addChannel.invoke(player, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        msg.clear();
        msg.put("type", "playerJoinAndQuit");
        msg.put("player", event.getPlayer().getDisplayName());
        msg.put("msg", "退出了服务器");
        JSONObject jo = new JSONObject(msg);
        MsgTool.msgSend(Client.session, jo.toJSONString());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        msg.clear();
        msg.put("type", "playerDeath");
        msg.put("player", event.getEntity().getDisplayName());
        msg.put("msg", event.getDeathMessage());
        JSONObject jo = new JSONObject(msg);
        MsgTool.msgSend(Client.session, jo.toJSONString());
    }


}
