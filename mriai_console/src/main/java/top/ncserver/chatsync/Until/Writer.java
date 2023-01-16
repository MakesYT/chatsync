package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import top.ncserver.Chatsync;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ncserver.Chatsync.bot;


public class Writer extends Thread implements Runnable{

    Pattern p = Pattern.compile("[(/+)\u4e00-\u9fa5(+*)]");
    public static String getEncode() {
        if (Pattern.matches("Linux.*", System.getProperty("os.name")))
            return "UTF-8";
        return "GBK";
    }
    Socket client;
    BufferedWriter bw;
    Listener listener;
    private OutputStream out=null;
    public Writer(Socket s) throws IOException {
        client=s;
        out=  s.getOutputStream();
         bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), getEncode()));
    }

    @Override
    public void run() {
        Chatsync.chatsync.getLogger().info("start catch");
        {
            Map<String,Object> msg1 = new HashMap<>();
            msg1.put("type","init");
            msg1.put("command",Config.INSTANCE.getSyncMsg());
            JSONObject jo= new JSONObject(msg1);
            Chatsync.chatsync.getLogger().info(jo.toJSONString());
            try {
                send(jo.toJSONString());
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                this.stop();
            }
        }
             listener= GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
                 if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                     Config.INSTANCE.setGroupID(event.getGroup().getId());
                     MsgTools.sendMsg("消息同步已绑定到此群");

                 }else
                if (event.getGroup().getId()==Config.INSTANCE.getGroupID()) {
                    try {
                        Map<String,Object> msg1 = new HashMap<>();
                        String msgString=event.getMessage().contentToString();
                       // msgString=msgString.replaceAll("\")
                        //At at=new At()
                        Matcher m = p.matcher(msgString.substring(1));
                        if(msgString.equals("/recon"))
                        {
                            MsgTools.sendMsg("消息同步正在重新连接.....");
                            client.close();
                        }else
                            if (msgString.startsWith("/")&&!m.lookingAt()){
                            if(event.getSender().getPermission().getLevel()>=1||msgString.equals("/ls")){
                                boolean baned=false;
                                String msg=event.getMessage().contentToString();
                                String command =msg.replaceFirst("/","");
                                for (String s : Config.INSTANCE.getBanCommand()) {
                                    if (command.matches(s)) {
                                        baned = true;
                                        break;
                                    }
                                }
                                if (!baned) {
                                    msg1.put("type","command");
                                    msg1.put("sender",event.getSenderName()+"("+event.getSender().getId()+")");
                                    msg1.put("command",msg);
                                    JSONObject jo= new JSONObject(msg1);
                                    Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                    send(jo.toJSONString());
                                }else MsgTools.sendMsg("该命令已被屏蔽");

                            }else if (msgString.contains("/LS")||msgString.contains("/IS")||msgString.contains("/Is")){
                                MsgTools.sendMsg("PS:正确的命令为/ls(均为小写.其大写形式为/LS)");

                            }else{
                                MsgTools.sendMsg("你无权执行"+msgString);

                            }
                                } else if (Config.INSTANCE.getSyncMsg())
                                {
                                    msg1.put("type","msg");
                                    msg1.put("permission",event.getSender().getPermission().getLevel());
                                    msg1.put("sender",event.getSenderName());
                                    String miraiCode=event.getMessage().toString();
                                    Chatsync.chatsync.getLogger().debug(miraiCode);
                                    StringBuilder msgBuilder=new StringBuilder();
                                    if (miraiCode.contains("[mirai:quote:["))
                                    {
                                        Chatsync.chatsync.getLogger().debug("catch");
                                        QuoteReply quote = event.getMessage().get(QuoteReply.Key);
                                        At at=new At(quote.getSource().getFromId());
                                        msgBuilder.append("\u00A75 回复了\n\u00A73");
                                        msgBuilder.append(at.getDisplay(event.getGroup()).replaceFirst("@","["));
                                        msgBuilder.append("]:");
                                        msgBuilder.append(quote.getSource().getOriginalMessage()+"\n\u2191---");
                                    }
                                    if (miraiCode.contains("[mirai:at:")){//at处理辣鸡Mirai BUG不修
                                        int t=0;
                                        while(miraiCode.indexOf("[mirai:at:",t)!=-1)
                                        {
                                            t=miraiCode.indexOf("[mirai:at:",t);
                                            long qqCode= Long.parseLong(miraiCode.substring(t+10,miraiCode.indexOf("]",t)));
                                            At at=new At(qqCode);
                                            msgBuilder.append(msgString.replaceFirst("@"+qqCode,at.getDisplay(event.getGroup())));
                                            t=miraiCode.indexOf("]",t);
                                        }
                                        msg1.put("msg",msgBuilder);
                                    }else
                                    msg1.put("msg",msgString);
                                    JSONObject jo= new JSONObject(msg1);
                                    Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                    send(jo.toJSONString());
                                }

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        this.stop();
                    }
                }
            });
            //listener.start();
        while(client.isConnected()){try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            listener.complete();
            e.printStackTrace();
        }}
    }
    public void send(String msg) throws InterruptedException, IOException {

        while (out==null||!client.isConnected()){}//

        bw.write(msg+'\n');
        try {
            bw.flush();
        } catch (IOException e) {
            listener.complete();
           this.stop();
        }
    }

}
