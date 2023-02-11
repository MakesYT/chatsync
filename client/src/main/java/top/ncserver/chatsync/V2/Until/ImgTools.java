package top.ncserver.chatsync.V2.Until;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.ncserver.chatsync.Chatsync;

import java.nio.charset.StandardCharsets;

public class ImgTools {
    private static final int IDX = 6969;
    private static final String channel = "chatimg:img";
    private static int ImgID=0;
    public static void sendImg(Player player,String base64){
        JSONObject json = new JSONObject();
        json.put("id",ImgID);
        json.put("base64imgdata","base64imgdata");
        int length=1024*1024;
        int n = (base64.length() + length - 1) / length; //获取整个字符串可以被切割成字符子串的个数
        String[] split = new String[n];
        for (int i = 0; i < n; i++) {
            if (i < (n - 1)) {
                split[i] = base64.substring(i * length, (i + 1) * length);
            } else {
                split[i] = base64.substring(i * length);
            }
        }
        for (int i = 0; i < split.length; i++) {
            JSONObject temp=json;
            temp.put("index",i);
            temp.put("data",split[i]);
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    send(player,temp.toJSONString());
                }
            }.runTaskAsynchronously(Chatsync.getPlugin(Chatsync.class));

        }
        player.sendMessage("[ImgID="+ImgID+"]");
        ImgID++;
    }
    private static void send(Player player, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(bytes.length + 1);
        buf.writeByte(IDX);
        buf.writeBytes(bytes);
        player.sendPluginMessage(Chatsync.getPlugin(Chatsync.class), channel, buf.array());
    }
    private String read(byte[] array) {
        ByteBuf buf = Unpooled.wrappedBuffer(array);
        if (buf.readUnsignedByte() == IDX) {
            return buf.toString(StandardCharsets.UTF_8);
        } else throw new RuntimeException();
    }

}
