package top.ncserver.chatsync;


import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Face;
import net.mamoe.mirai.message.data.PlainText;
import top.ncserver.chatsync.Until.MsgTools;
import top.ncserver.chatsync.Until.Reader;
import top.ncserver.chatsync.Until.ServerOffineTool;
import top.ncserver.chatsync.Until.Writer;

import java.io.IOException;
import java.net.Socket;

import static top.ncserver.Chatsync.bot;


public class Server implements Runnable {
    private Socket client = null;

    public Server(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        try {
            System.out.println("连接成功");
            ServerOffineTool.stop();
            Writer writer = new Writer(client);
            Reader reader = new Reader(client, writer);

            reader.start();
            writer.start();
            if (bot!=null){
                MsgTools.sendMsgMessageChain(new PlainText("服务器有了,别问我几个月的,我也不知道").plus(new Face(178)));
            }
            //writer.send("server");
            while(client.isConnected()&& reader.isAlive()&& writer.isAlive()){
                //锁线程
                Thread.sleep(50);
            }
            if (!reader.isAlive())
                System.out.println("reader crash");
            if (!writer.isAlive())
                System.out.println("writer crash");
            if (bot!=null){
                MsgTools.sendMsgMessageChain(new PlainText("服务器没了,等重启吧").plus(new Face(173)).plus(new Face(174)));
            }
            ServerOffineTool.init();
            System.out.println("连接丢失");
            reader.stop();
            writer.stop();
            client.close();

        }catch (IOException | InterruptedException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
