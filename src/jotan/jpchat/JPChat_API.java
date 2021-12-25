package jotan.jpchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import jotan.jpchat.datamanage.Player_Data_Manager;
import jotan.jpchat.datamanage.Player_Data_Manager.JPChat_Player_Data;

public class JPChat_API {

	public static String get_name(Player p) {
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
		return jppd.getNick_name();
	}

	public static ChatColor getColor(Player p) {
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
		return jppd.getColor();
	}

	public static JPChat_Player_Data get_JPChat_Player_Data(Player p) {
		return Player_Data_Manager.get_Player_Data(p);
	}

}
