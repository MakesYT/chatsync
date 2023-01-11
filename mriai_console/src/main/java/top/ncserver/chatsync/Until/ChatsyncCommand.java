package top.ncserver.chatsync.Until;

import kotlin.ParameterName;
import net.mamoe.mirai.console.command.Command;
import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import top.ncserver.Chatsync;

public final  class ChatsyncCommand extends JCompositeCommand {

    public ChatsyncCommand() {
        super(Chatsync.INSTANCE, "chatsync");

        this.setDescription("设置消息同步有关的命令输入/chatsync help获取帮助");

        // ...
    }


    @Description("设置消息同步监听端口")
    @SubCommand("port")
    public void port(CommandContext context,int port) {
        Config.INSTANCE.setPort(port);
        System.out.println("端口更改为"+port+"请重启Mirai以应用更改");
    }
    @Description("设置消息同步的QQ群ID")

    @SubCommand("groupid")
    public void port(CommandContext context,Long id) {
        Config.INSTANCE.setGroupID(id);
        System.out.println("消息同步群已更改为"+id);
    }
    @Description("设置玩家消息的格式[%s表示发送消息的玩家的名称,%msg表示发送的消息]")
    @SubCommand("msgstyle")
    public void msgstyle(CommandContext context,String style) {
        Config.INSTANCE.setMsgStyle(style);
        System.out.println("消息格式已更改为"+style);
    }
}
