package top.ncserver.chatsync;

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
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Chatsync extends JavaPlugin implements Listener {
        public Logger logger=this.getLogger();
    Client c;
    Map<String,Object> msg = new HashMap<>();
    @Override
        public void onEnable() {
        Bukkit.getPluginCommand("qqmsg").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
            logger.info("聊天同步插件已加载");
        logger.info("尝试连接");
        System.out.println("连接丢失,重新连接");
        Object[] players = this.getServer().getOnlinePlayers().toArray();
        for (Object player : players) {
            ((Player) player).getPlayer().sendMessage("§a[消息同步]消息同步插件加载成功,当前版本:"+getPlugin(this.getClass()).getDescription().getVersion());
        }
         c=new Client();
        c.runTaskAsynchronously(this);

    }
        @Override
        public void onDisable() {
             c.cancel();

            Client.reader.stop();
            Client.writer.stop();

        }
        @Override
        public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
            if(!commandSender.hasPermission("chatsync.qqmsg")) {
                commandSender.sendMessage("§c您没有这个命令的权限");
                return true;
            }
            if(strings.length == 0) {
                commandSender.sendMessage("§c这个命令需要参数！");
                return true;
            }
        logger.info("[发送消息]"+strings[0]);
        msg.clear();
        msg.put("type","obRe");
        msg.put("msg",strings[0]);

        JSONObject jo= new JSONObject(msg);
        try {
            Client.writer.send(jo.toJSONString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return true;
        }
        @EventHandler
        public void onChat(AsyncPlayerChatEvent event) throws IOException, InterruptedException {
            this.getLogger().info("["+event.getPlayer()+"]:"+event.getMessage());
            msg.clear();
            msg.put("type","msg");
            msg.put("sender",event.getPlayer().getDisplayName());
            msg.put("msg",event.getMessage());
            JSONObject jo= new JSONObject(msg);
            Client.writer.send(jo.toJSONString());

        }
        @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException, InterruptedException {
            msg.clear();
            msg.put("type","playerJoinAndQuit");
            msg.put("player",event.getPlayer().getDisplayName());
            msg.put("msg","加入了服务器");
            JSONObject jo= new JSONObject(msg);
            Client.writer.send(jo.toJSONString());
        }
        @EventHandler
    public void onQuit(PlayerQuitEvent event)throws IOException, InterruptedException {

            msg.clear();
            msg.put("type","playerJoinAndQuit");
            msg.put("player",event.getPlayer().getDisplayName());
            msg.put("msg","退出了服务器");
            JSONObject jo= new JSONObject(msg);
            Client.writer.send(jo.toJSONString());
        }

        @EventHandler
    public void onDeath(PlayerDeathEvent event)throws IOException, InterruptedException{
            msg.clear();
            msg.put("type","playerDeath");
            msg.put("player",event.getEntity().getDisplayName());
            msg.put("msg",event.getDeathMessage());
            JSONObject jo= new JSONObject(msg);
            Client.writer.send(jo.toJSONString());
        }







}
