package top.ncserver;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.Command;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import top.ncserver.chatsync.Server;
import top.ncserver.chatsync.Until.ChatsyncCommand;
import top.ncserver.chatsync.Until.Config;
import top.ncserver.chatsync.Until.MsgTools;
import top.ncserver.chatsync.Until.ServerOffineTool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class Chatsync extends JavaPlugin {
    public static final Chatsync INSTANCE = new Chatsync();

    private Chatsync() {
        super(new JvmPluginDescriptionBuilder("top.ncserver.chatsync", "0.1.0")
                .name("chatsync")
                .author("makesyt")
                .build());
    }
    public static  Bot bot;
    public static  Chatsync chatsync;
    @Override
    public void onEnable() {
        chatsync=this;
        CommandManager.INSTANCE.registerCommand(new ChatsyncCommand(),true);
        this.reloadPluginConfig(Config.INSTANCE);
        getLogger().info("Plugin loaded!");
        bot = null;
        getLogger().info("机器人加载完成,开始在127.0.0.1:"+Config.INSTANCE.getPort()+"创建socke服务器");
        ServerOffineTool.init();
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, (event) -> {
            bot= Bot.getInstances().get(0);
            MsgTools.sendMsg("消息同步QQ侧已加载");

        });
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, (event) -> {
            bot= null;
        });
        if ( Bot.getInstances().size()>0){
            bot= Bot.getInstances().get(0);
            MsgTools.sendMsg("消息同步QQ侧已加载");

        }

        new Thread(() -> {
            Socket client = null;
            ServerSocket server = null;
            try {
                server = new ServerSocket(Config.INSTANCE.getPort());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while(true){
                //等待客户端的连接，如果没有获取连接
                try {
                    client = server.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //为每个客户端连接开启一个线程
                new Thread(new Server(client)).start();
            }
        }, "MyThread").start();
    }

}