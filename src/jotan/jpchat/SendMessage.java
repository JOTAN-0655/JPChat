package jotan.jpchat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import bot.Discord_Bot_API;
import jotan.jpchat.Group.Group_Manager;
import jotan.jpchat.Group.Group_Manager.Chat_Group;
import jotan.jpchat.datamanage.Player_Data_Manager;
import jotan.jpchat.datamanage.Player_Data_Manager.JPChat_Player_Data;

public class SendMessage {

	//0 = minecraft
	//1 = ??
	//2 =
	public static List<String> get_Message(Player p,String message,String original,Chat_Group cg) {
		World wo = p.getWorld();
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
		String player_prefix = "[" + jppd.getColor() + jppd.getNick_name() + ChatColor.RESET + "]";
		String world_prefix = "(" + ChatColor.GRAY + wo.getName() + ChatColor.RESET + ")";

		String chat_group_prefix = "";
		if(cg != null) {
			if(cg.getNick_name().length() == 0) {
				chat_group_prefix = "(" + ChatColor.GRAY + cg.getGroup_name() + ChatColor.RESET + ")";
			}else {
				chat_group_prefix = "(" + ChatColor.GRAY + cg.getNick_name() + ChatColor.RESET + ")";
			}
		}

		String original_suffix = "";
		if(original != null) original_suffix = ChatColor.GRAY + "(" + original + ")";

		String final_message = player_prefix + world_prefix + chat_group_prefix + message + original_suffix;

		List<String> data = new ArrayList<String>();

		data.add(final_message);
		data.add(player_prefix);
		data.add(world_prefix);
		data.add(chat_group_prefix);
		data.add(message);
		data.add(original_suffix);
		return data;
	}

	public static boolean send_to_Discord(String converted,String original,Player p) {
		World wo = p.getWorld();
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
		String name = jppd.getNick_name();
		if(Bukkit.getPluginManager().isPluginEnabled("DISCORDplugin")) {
			if(original != null && original.startsWith("@")) {
				int start = original.indexOf("@");
				String mention_to = "";
				for(int i = 0; i < original.length()-start;i++) {
					if(original.charAt(i) == ' ') {
						break;
					}else {
						mention_to += original.charAt(i);
					}
				}
				Discord_Bot_API.send_message_server_chat_mention(mention_to, name + "からメッセージです:" + converted);
			}else {
				if(converted == null) converted = original;
				Discord_Bot_API.send_message_server_chat("[" + name + "]" + "(" + wo.getName() + ")" + converted);
			}
			return true;
		}
		return false;
	}

	public static boolean send_Message(Player p,String message,String original) {
		Chat_Group cg = Group_Manager.get_Group(p);
		List<String> data = get_Message(p,message,original,cg);

		String log = p.getName() + "," + p.getWorld().getName() + "," + message + "," + original;
		if(cg != null) log += "," + cg.getGroup_name();
		JPChat.getInstance().getLogger().info(log);

		if(cg == null) {
			send_to_Discord(message,original,p);
			for(Player online : Bukkit.getOnlinePlayers()) {
				Chat_Group cg_x = Group_Manager.get_Group(online);
				if(cg_x==null || !cg_x.isSeparate_global()) {
					online.sendMessage(data.get(0));
				}
			}
			return true;
		}else {
			List<UUID> players = cg.getPlayers();
			for(UUID group_uuid : players) {
				Player group_player = Bukkit.getPlayer(group_uuid);
				if(group_player == null) continue;
				group_player.sendMessage(data.get(0));
			}
			return true;
		}
	}

}
