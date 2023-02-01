package top.ncserver.chatsync.Until;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import top.ncserver.Chatsync;

import java.util.List;

public final  class ChatsyncCommand extends JCompositeCommand {

    public ChatsyncCommand() {
        super(Chatsync.INSTANCE, "chatsync");

        this.setDescription("设置消息同步有关的命令输入/help获取帮助");

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
    @Description("设置是否开启消息同步")
    @SubCommand("syncmsg")
    public void syncMsg(CommandContext context,boolean f) {
        Config.INSTANCE.setSyncMsg(f);
        if (f){
            System.out.println("已开启消息同步");
        }else
            System.out.println("已关闭消息同步");
    }
    @Description("设置屏蔽指令(支持正侧表达式)")
    @SubCommand("bancommand")
    public void banCommand(CommandContext context,String f,String command) {
        if (f.equals("add")){
            List<String> list = Config.INSTANCE.getBanCommand();
            list.add(command);
            Config.INSTANCE.setBanCommand(list);
            System.out.println("当前屏蔽词有(支持正侧表达式):");
            System.out.println(Config.INSTANCE.getBanCommand());
        }else if(f.equals("remove")){
            List<String> list = Config.INSTANCE.getBanCommand();
            if (list.remove(command)) {
                Config.INSTANCE.setBanCommand(list);
                System.out.println("当前屏蔽词有(支持正侧表达式):");
                System.out.println(Config.INSTANCE.getBanCommand());
            }else System.out.println("删除失败,屏蔽指令可能不存在");
        }else if (f.equals("list")){
            System.out.println("当前屏蔽词有(支持正侧表达式):");
            System.out.println(Config.INSTANCE.getBanCommand());
        }else {
            System.out.println("/chatsync bancommand add");
            System.out.println("/chatsync bancommand remove");
            System.out.println("/chatsync bancommand list");
        }
    }
}
