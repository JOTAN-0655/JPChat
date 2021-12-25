package jotan.jpchat.datamanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import jotan.jpchat.convert.Translate.Locale_ID_Name;
import jotan.jpchat.datamanage.yaml.Player_Data_Yaml;

public class Player_Data_Manager {

	private static List<JPChat_Player_Data> player_data = new ArrayList<JPChat_Player_Data>();

	public static List<JPChat_Player_Data> getPlayer_data() {
		return player_data;
	}

	public static void setPlayer_data(List<JPChat_Player_Data> player_data) {
		Player_Data_Manager.player_data = player_data;
	}

	public static class JPChat_Player_Data implements ConfigurationSerializable{
		String nick_name;
		ChatColor color;
		Locale_ID_Name talk_language;
		Locale_ID_Name translate_language;
		UUID uuid;

		public UUID getUuid() {
			return uuid;
		}
		public void setUuid(UUID uuid) {
			this.uuid = uuid;
		}
		public String getNick_name() {
			return nick_name;
		}
		public void setNick_name(String nick_name) {
			this.nick_name = nick_name;
		}
		public ChatColor getColor() {
			return color;
		}
		public void setColor(ChatColor color) {
			this.color = color;
		}
		public Locale_ID_Name getTalk_language() {
			return talk_language;
		}
		public void setTalk_language(Locale_ID_Name talk_language) {
			this.talk_language = talk_language;
		}
		public Locale_ID_Name getTranslate_language() {
			return translate_language;
		}
		public void setTranslate_language(Locale_ID_Name translate_language) {
			this.translate_language = translate_language;
		}

		@Override
		public Map<String, Object> serialize() {
			HashMap<String, Object> serializer = new HashMap<>(); // Create a map that will be used to serialize the class's fields
			serializer.put("nick_name", nick_name); // This is a 'special' type of object, so it will be 'converted' into a string
			serializer.put("color",this.color.name());
			serializer.put("talk_language", this.talk_language.toString());
			serializer.put("translate_language", this.translate_language.toString());
			serializer.put("uuid", this.getUuid().toString());
	        return serializer;
		}

		public static JPChat_Player_Data deserialize(Map<String,Object> serial_data) {
			JPChat_Player_Data jppd = new JPChat_Player_Data();
			jppd.setNick_name((String) serial_data.get("nick_name"));
			jppd.setColor(ChatColor.valueOf((String) serial_data.get("color")));
			jppd.setTalk_language(Locale_ID_Name.valueOf((String) serial_data.get("talk_language")));
			jppd.setTranslate_language(Locale_ID_Name.valueOf((String) serial_data.get("translate_language")));
			jppd.setUuid(UUID.fromString((String) serial_data.get("uuid")));
			return jppd;
		}
	}

	//CONFIG
	public static void save_all() {
		Player_Data_Yaml pdy = new Player_Data_Yaml();
		FileConfiguration config = pdy.getConfig();

		config.set("Player_Data", player_data);

		pdy.save_Yaml();
	}

	public static void load_all() {
		Player_Data_Yaml pdy = new Player_Data_Yaml();
		FileConfiguration config = pdy.getConfig();

		@SuppressWarnings("unchecked")
		List<JPChat_Player_Data> jppd = (List<JPChat_Player_Data>) config.getList("Player_Data");
		if(jppd == null) return;
		player_data = jppd;
	}

	//GET DATA
	public static JPChat_Player_Data get_Player_Data(Player p) {
		for(JPChat_Player_Data d : player_data) {
			if(d.getUuid().equals(p.getUniqueId())) return d;
		}
		return get_New_Player_Data(p);
	}

	public static JPChat_Player_Data get_New_Player_Data(Player p) {
		JPChat_Player_Data jppd = new JPChat_Player_Data();
		jppd.setNick_name(p.getName());
		jppd.setUuid(p.getUniqueId());
		jppd.setColor(ChatColor.GREEN);
		jppd.setTranslate_language(Locale_ID_Name.Japanese);
		jppd.setTalk_language(Locale_ID_Name.Japanese);

		getPlayer_data().add(jppd);
		return get_Player_Data(p);
	}

}
