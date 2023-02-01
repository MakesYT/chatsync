import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.plugins.HeartPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.extension.protocol.StringProtocol;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.chatsync.V2.Until.MsgTool;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AioClient {
    public static AioSession session;
    public static boolean isConnected=false;

    public static void main(String[] args) {
        while (!isConnected){

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            connection("127.0.0.1", 1111);
        }
    }
    public static void connection(String host, int port) {
        try {
            AbstractMessageProcessor<String> processor = new AbstractMessageProcessor<String>(){
                @Override
                public void process0(AioSession aioSession, String msg) {
                    try {
                        MsgTool.msgRead(session, msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                @Override
                public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                    if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)){
                        isConnected=true;
                    }else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)){
                        System.out.println(throwable.getMessage());
                        System.out.println(("连接丢失"));
                        isConnected=false;
                        while (!isConnected) {

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            connection("127.0.0.1", 1111);
                        }
                        //logger.info("2");

                    }
                }
            };
            processor.addPlugin(new HeartPlugin<String>(5, 9, TimeUnit.SECONDS) {
                @Override
                public void sendHeartRequest(AioSession session) throws IOException {
                    System.out.println(1);
                    WriteBuffer writeBuffer = session.writeBuffer();
                    byte[] content = "heart message".getBytes();
                    writeBuffer.writeInt(content.length);
                    writeBuffer.write(content);
                    writeBuffer.flush();
                }

                @Override
                public boolean isHeartMessage(AioSession session, String msg) {
                    return "heart messageS".equals(msg);
                }
            });

            AioQuickClient client = new AioQuickClient(host, port, new StringProtocol(), processor);
            session = client.start();
            System.out.println(("连接成功"));
            //System.out.println("§4警告:消息同步连接丢失,请稍后再试或者联系管理员解决");


        } catch (IOException ignored) {


        }

    }
}
