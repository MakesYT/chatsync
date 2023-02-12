package top.ncserver.chatsync.V2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kotlin.text.Charsets;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.Chatsync;
import top.ncserver.chatsync.Until.Config;
import top.ncserver.chatsync.Until.TextToImg;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ncserver.Chatsync.bot;
import static top.ncserver.chatsync.Until.ColorCodeCulling.CullColorCode;

public class MsgTools {
    static Pattern p = Pattern.compile("[(/+)\u4e00-\u9fa5(+*)]");
    static HttpClient client;
    public  static void listenerInit(){
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .cookieHandler(new CookieManager())
                .build();
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
            if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                Config.INSTANCE.setGroupID(event.getGroup().getId());
                QQsendMsg("消息同步已绑定到此群");

            }else if (Chatsync.INSTANCE.isConnected){
                if (event.getGroup().getId()==Config.INSTANCE.getGroupID()) {
                    Map<String,Object> msg1 = new HashMap<>();
                    String msgString=event.getMessage().contentToString();
                    // msgString=msgString.replaceAll("\")
                    //At at=new At()
                    Matcher m = p.matcher(msgString.substring(1));
                    if(msgString.equals("/recon"))
                    {
                        QQsendMsg("消息同步正在重新连接.....");
                        Chatsync.session.close();
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
                                msgSend(Chatsync.session,jo.toJSONString());
                            }else QQsendMsg("该命令已被屏蔽");

                        }else if (msgString.contains("/LS")||msgString.contains("/IS")||msgString.contains("/Is")){
                            QQsendMsg("PS:正确的命令为/ls(均为小写.其大写形式为/LS)");

                        }else{
                            QQsendMsg("你无权执行"+msgString);

                        }
                    } else if (Config.INSTANCE.getSyncMsg())
                    {
                        StringBuilder sb= new StringBuilder();
                        JSONArray msg = JSON.parseArray(MessageChain.serializeToJsonString(event.getMessage()));
                        if (event.getMessage().contains(QuoteReply.Key)){
                            QuoteReply quote = event.getMessage().get(QuoteReply.Key);
                            At at=new At(quote.getSource().getFromId());
                            StringBuilder msgBuilder=new StringBuilder();
                            msgBuilder.append("\u00A75 回复了\n\u00A73");
                            msgBuilder.append(at.getDisplay(event.getGroup()).replaceFirst("@","["));
                            msgBuilder.append("]:");
                            msgBuilder.append(quote.getSource().getOriginalMessage());
                            sb.append("\u2191---");
                            msg1.put("type","remsg");
                            msg1.put("permission",event.getSender().getPermission().getLevel());
                            msg1.put("sender",event.getSenderName());
                            msg1.put("msg",msgBuilder);
                            JSONObject jo= new JSONObject(msg1);
                            Chatsync.chatsync.getLogger().info(jo.toJSONString());
                            msgSend(Chatsync.session,jo.toJSONString());
                        }
                        JSONArray originalMessage=((JSONObject)msg.get(0)).getJSONArray("originalMessage");
                        msg1.clear();

                        for (Object o : originalMessage) {
                            System.out.println(((JSONObject) o).toJSONString());
                            switch (((JSONObject)o).getString("type")){
                                case "PlainText":{
                                    sb.append(((JSONObject)o).getString("content"));
                                    break;
                                }
                                case "At":{
                                    At at=new At(((JSONObject)o).getLong("target"));
                                    sb.append(at.getDisplay(event.getGroup()));
                                    break;
                                }
                                case "AtAll":{
                                    sb.append("@全体成员");
                                    break;
                                }
                                case "Image":{
                                    Image img=Image.fromId(((JSONObject)o).getString("imageId"));
                                    HttpRequest request = HttpRequest.newBuilder()
                                            .version(HttpClient.Version.HTTP_1_1)
                                            .GET()
                                            .timeout(Duration.ofSeconds(2))
                                            .uri(URI.create(Image.queryUrl(img)))
                                            .build();
                                    try {
                                        System.out.println(Image.queryUrl(img));
                                        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                                        msg1.put("type","img");
                                        msg1.put("permission",event.getSender().getPermission().getLevel());
                                        msg1.put("sender",event.getSenderName());
                                        msg1.put("data",Base64.getEncoder().encodeToString(response.body()));
                                        JSONObject jo= new JSONObject(msg1);
                                        Chatsync.chatsync.getLogger().info(jo.toJSONString());
                                        msgSend(Chatsync.session,jo.toJSONString());
                                    } catch (IOException | InterruptedException e) {
                                        Chatsync.chatsync.getLogger().info("图片请求失败");
                                    }
                                    break;
                                }
                                case "QuoteReply":{
                                    break;
                                }
                                default:{

                                    MessageChain messageChain= MessageChain.deserializeFromJsonString("["+((JSONObject)o).toJSONString()+"]");
                                    sb.append(messageChain.get(0).contentToString());
                                }
                            }
                        }
                        msg1.put("type","msg");
                        msg1.put("permission",event.getSender().getPermission().getLevel());
                        msg1.put("sender",event.getSenderName());
                        msg1.put("msg",sb);
                        JSONObject jo= new JSONObject(msg1);
                        Chatsync.chatsync.getLogger().info(jo.toJSONString());
                        msgSend(Chatsync.session,jo.toJSONString());

                    }

                }
            }else{
                if (Config.INSTANCE.getGroupID()==0L&&event.getSender().getPermission().getLevel()>=1&&event.getMessage().contentToString().equals("/chatsync bind this")){
                    Config.INSTANCE.setGroupID(event.getGroup().getId());
                    MsgTools.QQsendMsg("消息同步已绑定到此群");

                }else
                if (event.getGroup().getId()==Config.INSTANCE.getGroupID()){
                    String msg=event.getMessage().contentToString();
                    if (msg.contains("/ls")||msg.contains("/list")) {
                        MsgTools.QQsendMsg("抱歉,服务器处于离线状态");
                    }else if (msg.contains("/LS")||msg.contains("/IS")||msg.contains("/Is")){
                        MsgTools.QQsendMsg("抱歉,服务器处于离线状态\nPS:正确的命令为/ls(均为小写.其大写形式为/LS)");
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
                        MsgTools.QQsendImg(file);
                    }
                }
            }

        });
    }
    public static void msgRead(AioSession session, String msgJ) throws IOException {
        if (bot!=null){
            System.out.println(msgJ);
            JSONObject jsonObject = JSONObject.parseObject(msgJ);
            if (Chatsync.INSTANCE.isConnected){
                switch (jsonObject.getString("type")){
                    case "msg":
                        if (Config.INSTANCE.getSyncMsg()){
                            System.out.println("["+jsonObject.getString("sender")+"]:"+jsonObject.getString("msg"));
                            QQsendMsg(Config.INSTANCE.getMsgStyle().replaceAll("%s",jsonObject.getString("sender")).replaceAll("%msg",jsonObject.getString("msg")));
                        }
                        break;
                    case "playerJoinAndQuit":
                        QQsendMsg("玩家"+CullColorCode(jsonObject.getString("player"))+jsonObject.getString("msg"));
                        break;
                    case "playerList":
                        QQsendMsg("当前有"+jsonObject.getString("online")+"位玩家在线\n"+jsonObject.getString("msg"));
                        break;
                    case "command":
                        QQsendMsg("接收到命令回馈,正在渲染图片");
                        long start = System.currentTimeMillis();
                        File file= TextToImg.toImg(jsonObject.getString("command"));
                        long finish = System.currentTimeMillis();
                        long timeElapsed = finish - start;
                        QQsendMsg("完成,耗时"+timeElapsed+"ms,上传中");
                        QQsendImg(file);
                        System.gc();
                        break;
                    case "serverCommand":
                        QQsendMsg("注意服务器执行："+jsonObject.getString("command")+"\n注意服务器安全");
                        break;
                    case "playerDeath":
                    case "obRe":
                        QQsendMsg(CullColorCode(jsonObject.getString("msg")));
                        break;
                }
            }

        }

    }
    public static void msgSend(AioSession session, String msg) {
        try {
            WriteBuffer writeBuffer = session.writeBuffer();
            byte[] content = msg.getBytes(Charsets.UTF_8);
            writeBuffer.writeInt(content.length);
            writeBuffer.write(content);
            writeBuffer.flush();
        }catch (IOException e) {

        }

    }
    public static void QQsendMsg(String msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(new PlainText(msg));
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }
    public static void QQsendMsgMessageChain(MessageChain msg){
        if (Config.INSTANCE.getGroupID()!=0L) {
            bot.getGroup(Config.INSTANCE.getGroupID()).sendMessage(msg);
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,以启用消息同步");
        }
    }
    public static void QQsendImg(File file){
        if (Config.INSTANCE.getGroupID()!=0L) {
            ExternalResource.sendAsImage(file,bot.getGroup(Config.INSTANCE.getGroupID()));
        }else{
            Chatsync.chatsync.getLogger().info("请绑定QQ群,已启用消息同步");
        }
    }
}
