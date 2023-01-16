package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

import static top.ncserver.Chatsync.bot;
import static top.ncserver.chatsync.Until.ColorCodeCulling.CullColorCode;


public class Reader extends Thread implements Runnable{
    Socket in;
    Writer out;
    BufferedReader b;
    public static String getEncode() {
        if (Pattern.matches("Linux.*", System.getProperty("os.name")))
            return "UTF-8";
        return "GBK";
    }
    public Reader(Socket in, Writer out) throws IOException, ClassNotFoundException {
        this.in=in;
        this.out=out;
        this.b=new BufferedReader(new InputStreamReader(in.getInputStream(), getEncode()));
    }
    @Override
    public void run() {
        while(true)
        {
            try {
                String echo = b.readLine();
                if (bot!=null){
                    System.out.println(echo);
                    JSONObject jsonObject = JSONObject.parseObject(echo);
                    switch (jsonObject.getString("type")){
                        case "msg":
                            if (Config.INSTANCE.getSyncMsg()){
                                System.out.println("["+jsonObject.getString("sender")+"]:"+jsonObject.getString("msg"));
                                MsgTools.sendMsg(Config.INSTANCE.getMsgStyle().replaceAll("%s",jsonObject.getString("sender")).replaceAll("%msg",jsonObject.getString("msg")));
                            }
                           break;
                        case "playerJoinAndQuit":
                            MsgTools.sendMsg("玩家"+CullColorCode(jsonObject.getString("player"))+jsonObject.getString("msg"));
                            break;
                        case "playerList":
                            MsgTools.sendMsg("当前有"+jsonObject.getString("online")+"位玩家在线\n"+jsonObject.getString("msg"));
                            break;
                        case "command":
                            MsgTools.sendMsg("接收到命令回馈,正在渲染图片");
                            long start = System.currentTimeMillis();
                            File file=TextToImg.toImg(jsonObject.getString("command"));
                            long finish = System.currentTimeMillis();
                            long timeElapsed = finish - start;
                            MsgTools.sendMsg("完成,耗时"+timeElapsed+"ms,上传中");
                            MsgTools.sendImg(file);



                            System.gc();
                             break;
                        case "serverCommand":
                            MsgTools.sendMsg("注意服务器执行："+jsonObject.getString("command")+"\n注意服务器安全");

                            break;
                        case "playerDeath":
                        case "obRe":
                            MsgTools.sendMsg(CullColorCode(jsonObject.getString("msg")));

                            break;
                }

                    }
            } catch (IOException e) {
                e.printStackTrace();
                this.stop();
                break;
            }


        }
    }
}
