package top.ncserver.chatsync.Until;

import java.io.Serializable;

public class Msg implements Serializable {
    private static final long serialVersionUID = 8625279621236358825L;
    public static String sender;
    public static String msg;
    public Msg(String s,String msg1){
        sender=s;
        msg =msg1;
    }

    public  String getMsg() {
        return msg;
    }

    public  String getSender() {
        return sender;
    }
}
