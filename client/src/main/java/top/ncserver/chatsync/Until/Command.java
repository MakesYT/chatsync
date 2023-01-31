package top.ncserver.chatsync.Until;/*
@author：MakesYT
@program：Ncharge
*/

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;
import top.ncserver.chatsync.Client;
import top.ncserver.chatsync.Chatsync;
import top.ncserver.chatsync.V2.Until.MsgTool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Command implements CommandExecutor {
	public Logger logger= Chatsync.getPlugin(Chatsync.class).getLogger();
	@Override
	public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
		logger.info("[发送消息]"+command);
		Map<String,Object> msg = new HashMap<>();
		msg.put("type","obRe");
		msg.put("msg",command);

		JSONObject jo= new JSONObject(msg);
		MsgTool.msgSend(Client.session, jo.toJSONString());
		return true;
	}
}
