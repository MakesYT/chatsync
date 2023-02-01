package top.ncserver.chatsync.Until;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ColorCodeCulling {
    public static String CullColorCode(String str){
        StringBuilder send_message=new StringBuilder(str);
        while (send_message.indexOf("ﾧ") != -1) {
            int i = send_message.indexOf("ﾧ");
            send_message.replace(i,i+2,"");
        }
        while (send_message.indexOf("§") != -1) {
            int i = send_message.indexOf("§");
            send_message.replace(i,i+2,"");
        }
        return send_message.toString();
    }
}
