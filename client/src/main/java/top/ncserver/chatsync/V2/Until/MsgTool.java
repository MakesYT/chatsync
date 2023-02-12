package top.ncserver.chatsync.V2.Until;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.chatsync.Chatsync;
import top.ncserver.chatsync.Client;
import top.ncserver.chatsync.Until.ConsoleSender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MsgTool {
    private static boolean syncMsg = true;
    public static void msgRead(AioSession session, String msgJ) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(msgJ);
        Object[] players = Chatsync.getPlugin(Chatsync.class).getServer().getOnlinePlayers().toArray();
        Map<String, Object> msg = new HashMap<>();
        switch (jsonObject.getString("type")){
            case "remsg":{
                if (syncMsg) {
                    Chatsync.getPlugin(Chatsync.class).logger.info(jsonObject.getString("msg"));
                    for (Object player : players) {
                        ((Player) player).getPlayer().sendMessage(jsonObject.getString("msg"));
                    }
                }
                break;
            }
            case "msg":{
                if (syncMsg)
                {
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
                break;
            }
            case "img":{
                String msg1=null;
                switch (jsonObject.getInteger("permission")) {

                    case 0: {
                        msg1 = "[玩家][" + jsonObject.getString("sender") + "]:" ;
                        break;
                    }
                    case 1: {
                        msg1 = "[\u00A7c管理员\u00A7r][" + jsonObject.getString("sender") + "]:";
                        break;
                    }
                    case 2: {
                        msg1 = "[\u00A76腐竹\u00A7r][" + jsonObject.getString("sender") + "]:" ;
                        break;
                    }
                }
                Chatsync.getPlugin(Chatsync.class).logger.info("收到图片");
                for (Object player : players) {
                    ((Player) player).getPlayer().sendMessage(msg1);
                }
                ImgTools.sendImg(players,jsonObject.getString("data"));
            }
            case "command":{
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
                    msgSend(session,jo.toJSONString());
                } else {
                    Chatsync.getPlugin(Chatsync.class).logger.info("QQ群[" + jsonObject.getString("sender") + "]执行了" + jsonObject.getString("command"));
                    msg.put("type", "command");
                    Bukkit.getScheduler().runTaskAsynchronously(Chatsync.getPlugin(Chatsync.class), () -> {
                        String cmd = jsonObject.getString("command").substring(1);
                        ConsoleSender sender = new ConsoleSender(Bukkit.getServer());
                        Bukkit.getScheduler().runTask(Chatsync.getPlugin(Chatsync.class), () -> Bukkit.dispatchCommand(sender, cmd));
                    });
                }
                break;
            }
            case "init":{
                syncMsg= jsonObject.getBoolean("command");
                if (!syncMsg){
                    HandlerList.unregisterAll((Listener) Chatsync.getPlugin(Chatsync.class));
                }
                break;
            }
        }


    }
    public static void msgSend(AioSession session, String msg) {
        if (Client.isConnected)
            try{
                WriteBuffer writeBuffer = session.writeBuffer();
                byte[] data = msg.getBytes(Charsets.UTF_8);
                writeBuffer.writeInt(data.length);
                writeBuffer.write(data);
                writeBuffer.flush();
            }catch (IOException e){



            }


    }
}
