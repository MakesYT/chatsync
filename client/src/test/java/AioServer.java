import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.buffer.BufferPagePool;
import org.smartboot.socket.extension.plugins.HeartPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import top.ncserver.chatsync.V2.Until.StringProtocol;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AioServer {
    public static void main(String[] args) throws IOException {
        AbstractMessageProcessor<String> processor = new AbstractMessageProcessor<String>() {
            @Override
            public void process0(AioSession aioSession, String s) {
                System.out.println(s);
            }

            @Override
            public void stateEvent0(AioSession aioSession, StateMachineEnum stateMachineEnum, Throwable throwable) {

            }
        };
        processor.addPlugin(new HeartPlugin<String>(10, 30, TimeUnit.SECONDS) {
            @Override
            public void sendHeartRequest(AioSession session) throws IOException {
                WriteBuffer writeBuffer = session.writeBuffer();
                byte[] content = "heart message".getBytes();
                writeBuffer.writeInt(content.length);
                writeBuffer.write(content);
            }

            @Override
            public boolean isHeartMessage(AioSession session, String msg) {
                return "heart message".equals(msg);
            }
        });

        AioQuickServer server = new AioQuickServer(1111, new StringProtocol(), processor);
        server.start();
    }
}
