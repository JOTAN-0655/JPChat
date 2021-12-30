package jotan.jpchat;

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
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class SendMessage {

	private static enum Where_Send{
		Discord,
		Minecraft
	}

	public static String get_Message(Player p,String message,String original,Chat_Group cg, Where_Send ws) {
		World wo = p.getWorld();
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);

		/*
		# You can use follow replaces
		# [prefix] - shows luckperms prefix.
		# [suffix] - shows luckperms suffix.
		# [message] - shows message. Almost everyone needs this.
		# [original_message] - shows message which is before converted by JPPlugin.
		# [world] - shows world name where player is.
		# [name] - shows players name.
		# [chat_group] - shows what chat group does player in.
		*/

		String prefix = "[invalid]";
		String suffix = "[invalid]";
		if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
			LuckPerms luckPerms = LuckPermsProvider.get();
			User user = luckPerms.getPlayerAdapter(Player.class).getUser(p);

			prefix = user.getCachedData().getMetaData().getPrefix();
			suffix = user.getCachedData().getMetaData().getSuffix();
		}

		String world_name = wo.getName();
		String user_name = jppd.getColor() + jppd.getNick_name() + ChatColor.RESET;
		String chat_group_prefix = "";
		if(cg != null) {
			if(cg.getNick_name().length() == 0)
				chat_group_prefix = cg.getGroup_name();
			else
				chat_group_prefix = cg.getNick_name();
		}

		String format = "Error";
		if(ws.equals(Where_Send.Minecraft))
			format = JPChat.getInstance().getConfig().getString("Minecraft_Message_Style");
		else
			format = JPChat.getInstance().getConfig().getString("Discord_Message_Style");

		format = format.replace("[prefix]", prefix);
		format = format.replace("[suffix]", suffix);
		format = format.replace("[message]", message);
		format = format.replace("[original_message]", original);
		format = format.replace("[world]", world_name);
		format = format.replace("[name]", user_name);
		format = format.replace("[chat_group]", chat_group_prefix);

		if(ws.equals(Where_Send.Minecraft)) {
			format = ChatColor.translateAlternateColorCodes('&', format);
		}else {
			while(true) {
				int at = format.indexOf('§');
				if(at == -1) break;
				format = format.substring(0, at) + format.substring(at+2);
			}
		}

		return format;
	}

	public static boolean send_to_Discord(String converted,String original,Player p) {
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
				Chat_Group cg = Group_Manager.get_Group(p);
				String message = get_Message(p,converted,original,cg,Where_Send.Discord);
				Discord_Bot_API.send_message_server_chat(message);
			}
			return true;
		}
		return false;
	}

	public static boolean send_Message(Player p,String message,String original) {
		Chat_Group cg = Group_Manager.get_Group(p);
		String data = get_Message(p,message,original,cg,Where_Send.Minecraft);

		//String log = p.getName() + "," + p.getWorld().getName() + "," + message + "," + original;
		//if(cg != null) log += "," + cg.getGroup_name();
		JPChat.getInstance().getLogger().info(data);

		if(cg == null) {
			send_to_Discord(message,original,p);
			for(Player online : Bukkit.getOnlinePlayers()) {
				Chat_Group cg_x = Group_Manager.get_Group(online);
				if(cg_x==null || !cg_x.isSeparate_global()) {
					online.sendMessage(data);
				}
			}
			return true;
		}else {
			List<UUID> players = cg.getPlayers();
			for(UUID group_uuid : players) {
				Player group_player = Bukkit.getPlayer(group_uuid);
				if(group_player == null) continue;
				group_player.sendMessage(data);
			}
			return true;
		}
	}

}
