package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.ncserver.chatsync.Client;
import top.ncserver.chatsync.Chatsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ReaderClient extends Thread implements Runnable {
  BufferedReader b;
  Writer out;

  public ReaderClient(BufferedReader buf) {
    this.b = buf;
  }

  public ReaderClient() {

  }

  public static String getEncode() {
    if (Pattern.matches("Linux.*", System.getProperty("os.name")))
      return "UTF-8";
    return "GBK";
  }
  public ReaderClient(Socket in, Writer out) throws IOException {
    this.b = new BufferedReader(new InputStreamReader(in.getInputStream(),getEncode()));
    this.out = out;
  }

  @Override
  public void run() {
    while (true) {
      try {
        String echo = b.readLine();
        JSONObject jsonObject = JSONObject.parseObject(echo);

        Object[] players = Chatsync.getPlugin(Chatsync.class).getServer().getOnlinePlayers().toArray();
        Map<String, Object> msg = new HashMap<>();
        if (jsonObject.getString("type").equals("command")) {
          if (jsonObject.getString("command").equals("/ls")) {
            Chatsync.getPlugin(Chatsync.class).logger.info("QQ群[" + jsonObject.getString("sender") + "]查询了玩家在线数量");
            msg.put("type", "playerList");
            msg.put("online", players.length);
            String list = null;
            for (Object player : players) {
              list = list + "," + ((Player) player).getPlayer().getDisplayName();
            }
            if (list != null) {
              msg.put("msg", list.substring(5));
            } else msg.put("msg", "无,惨兮兮");
            JSONObject jo = new JSONObject(msg);
            Client.writer.send(jo.toJSONString());
          } else {
            Chatsync.getPlugin(Chatsync.class).logger.info("QQ群[" + jsonObject.getString("sender") + "]执行了" + jsonObject.getString("command"));
            msg.put("type", "command");
            String w;
            StringBuilder send_message=null;
            Bukkit.getScheduler().runTaskAsynchronously(Chatsync.getPlugin(Chatsync.class), () -> {
                      String cmd = jsonObject.getString("command").substring(1);
                      ConsoleSender sender = new ConsoleSender(Bukkit.getServer());
                      Bukkit.getScheduler().runTask(Chatsync.getPlugin(Chatsync.class), () -> Bukkit.dispatchCommand(sender, cmd));
              });
          }
        } else {
          String msg1=null;
          switch (jsonObject.getInteger("permission")) {

            case 0: {
              msg1 = "[玩家][" + jsonObject.getString("sender") + "]:" + jsonObject.getString("msg");
              break;
            }
            case 1: {
              msg1 = "[\u00A7c管理员\u00A7r][" + jsonObject.getString("sender") + "]:" + jsonObject.getString("msg");
              break;
            }
            case 2: {
              msg1 = "[\u00A76腐竹\u00A7r][" + jsonObject.getString("sender") + "]:" + jsonObject.getString("msg");
              break;
            }
          }
          Chatsync.getPlugin(Chatsync.class).logger.info(msg1);
          for (Object player : players) {
            ((Player) player).getPlayer().sendMessage(msg1);
          }
        }
      } catch (IOException | InterruptedException e) {
        System.out.println(e);
        this.stop();
        break;
      }
    }
  }
}
