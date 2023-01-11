package top.ncserver.chatsync.Until;

public class ColorCodeCulling {
    public static String CullColorCode(String str){
        StringBuilder send_message=new StringBuilder(str);
        while (send_message.indexOf("§") != -1) {
            int i = send_message.indexOf("§");
            send_message.replace(i,i+2,"");
        }

        return send_message.toString();
    }
}
