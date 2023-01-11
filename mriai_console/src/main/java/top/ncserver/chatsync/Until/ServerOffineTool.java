package top.ncserver.chatsync.Until;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import top.ncserver.Chatsync;

import java.io.File;
import java.io.IOException;

import static top.ncserver.Chatsync.bot;


public class ServerOffineTool {
    static Listener listener;
    public static void stop(){
        listener.complete();
    }
    public static void init(){
        listener= GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
            if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                Config.INSTANCE.setGroupID(event.getGroup().getId());
                MsgTools.sendMsg("消息同步已绑定到此群");

            }else
            if (event.getGroup().getId()==Config.INSTANCE.getGroupID()){
                String msg=event.getMessage().contentToString();
                if (msg.contains("/ls")||msg.contains("/list")) {
                    MsgTools.sendMsg("抱歉,服务器处于离线状态");
                }else if (msg.contains("/LS")||msg.contains("/IS")||msg.contains("/Is")){
                    MsgTools.sendMsg("抱歉,服务器处于离线状态\nPS:正确的命令为/ls(均为小写.其大写形式为/LS)");
                }else if (msg.contains("%test")){
                    File file= null;
                    try {
                        file = TextToImg.toImg("------ ======= Help ======= ------\n/actionbarmsg [玩家名称/all" +
                                "] (-s:[秒]) [消息] - 给玩家发送actionbar消息\n/afk (-p:玩家名称) (原因) (-s) - 切换离开模式,可提供原因\n/afkcheck" +
                                " [玩家名称/all] - 检查玩家离开状态\n/air [玩家名称] [空气值] (-s) - 设置玩家空气值\n/alert [玩家名称] (原因" +
                                ") - 当玩家登录时提醒管理\n/alertlist - 列出所有记录的提醒\\n/aliaseditor (new) (alias-cmd) - 指令简写" +
                                "编辑\nFor next page perform cmi ? 2\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    MsgTools.sendImg(file);
                }
            }


        });
    }
}
