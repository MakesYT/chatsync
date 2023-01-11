package top.ncserver.chatsync.Until;


import org.bukkit.entity.Player;
import top.ncserver.chatsync.Client;
import top.ncserver.chatsync.Chatsync;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class Writer extends Thread implements Runnable{
    Socket client;
    private OutputStream out=null;
    BufferedWriter bw;

    public Writer() {

    }

    public static String getEncode() {
        if (Pattern.matches("Linux.*", System.getProperty("os.name")))
            return "UTF-8";
        return "GBK";
    }
    public Writer(Socket s) throws IOException {
        client=s;
        out=  s.getOutputStream();
        bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), getEncode()));
    }

    @Override
    public void run() {
        while (Chatsync.getPlugin(Chatsync.class).isEnabled()){
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

    }
    public void send(String msg) throws InterruptedException, IOException {
        if (out==null||!client.isConnected()||client.isClosed()||!Client.reader.isAlive()||!Client.writer.isAlive()||Client.client.isClosed()){
            System.out.println("§4警告:消息同步连接丢失,请稍后再试或者联系管理员解决");
            Object[] players = Chatsync.getPlugin(Chatsync.class).getServer().getOnlinePlayers().toArray();
            for (Object player : players) {
                ((Player) player).getPlayer().sendMessage("§4警告:消息同步连接丢失,请稍后再试或者联系管理员解决");
            }
        }//
        //System.out.println("sent");
            else
        try {
            bw.write(msg+'\n');
            bw.flush();
        } catch (IOException e) {
            client.close();
            this.stop();
        }
    }
}
