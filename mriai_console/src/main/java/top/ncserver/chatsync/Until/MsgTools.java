package top.ncserver.chatsync.Until;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import top.ncserver.Chatsync;

import java.io.File;

import static top.ncserver.Chatsync.bot;

public class MsgTools {
    public static void sendMsg(String msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(new PlainText(msg));
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }
    public static void sendMsgMessageChain(MessageChain msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(msg);
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }
    public static void sendImg(File file){
        if (Config.INSTANCE.getGroupID()!=0L) {
            ExternalResource.sendAsImage(file,bot.getGroup(Config.INSTANCE.getGroupID()));
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,已启用消息同步");
        }
    }
}
