package top.ncserver.chatsync;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import top.ncserver.chatsync.Until.ReaderClient;
import top.ncserver.chatsync.Until.Writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Client extends BukkitRunnable {
    public static Writer writer;
    public static ReaderClient reader;
    public static Socket client;
    public static  void connection(String host, int port)
    {
        try {
            reader=new ReaderClient();
            writer=new Writer();
              client = new Socket(host, port);
            while(!client.isConnected()){}
            System.out.println("连接成功");
            //System.out.println("§4警告:消息同步连接丢失,请稍后再试或者联系管理员解决");
            Object[] players = Chatsync.getPlugin(Chatsync.class).getServer().getOnlinePlayers().toArray();
            for (Object player : players) {
                ((Player) player).getPlayer().sendMessage("消息同步连接成功");
            }
             writer=new Writer(client);
             reader=new ReaderClient(client,writer);
            reader.start();
            writer.start();
            while(client.isConnected()&&reader.isAlive()&&writer.isAlive()){
                //锁线程
                Thread.sleep(50);
            }
            if (!reader.isAlive())
                System.out.println("reader crash");
            if (!writer.isAlive())
                System.out.println("writer crash");
            System.out.println("连接丢失,重新连接");
            for (Object player : players) {
                ((Player) player).getPlayer().sendMessage("§4警告:消息同步连接丢失");
            }
            reader.stop();
            writer.stop();
            client.close();
        } catch (IOException | InterruptedException e) {
            //System.out.println(e);
            //System.out.println("连接失败");
        }

    }
    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] arrayOfByte = new byte[63];
            int i;
            while ((i = inputStream.read(arrayOfByte)) > 0) {
                fileOutputStream.write(arrayOfByte, 0, i);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(Chatsync.getPlugin(Chatsync.class).getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copyFile(Chatsync.getPlugin(Chatsync.class).getResource("config.yml"), configFile);

            Chatsync.getPlugin(Chatsync.class).getLogger().info("File: 已生成 config.yml 文件");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        while(Chatsync.getPlugin(Chatsync.class).isEnabled())
        {
            connection(config.getString("ip"), config.getInt("port"));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
