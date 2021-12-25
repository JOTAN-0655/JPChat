package jotan.jpchat.datamanage;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import jotan.jpchat.datamanage.yaml.Dictionary_Config;

public class Dictionary_Manager {
	private static HashMap<String,String> dic = new HashMap<String,String>();

	public static void set_Dic(String wrong,String correct) {
		dic.put(wrong, correct);
	}

	public static String get_Dic(String matigae) {
		String correct = dic.get(matigae);
		if(correct == null) return matigae;
		else return correct;
	}

	public static boolean remove_Dic(String wrong) {
		return dic.remove(wrong) != null;
	}

	public static Set<String> get_Dic_Keys() {
		return dic.keySet();
	}

	//FROM CONFIG
	public static void load_Dic() {
		Dictionary_Config dc = new Dictionary_Config();
		FileConfiguration config = dc.getConfig();

		ConfigurationSection select = config.getConfigurationSection("Dic");
		if(select == null) return;
		Set<String> keys = select.getKeys(false);
		if(keys == null) return;
		for(String key : keys) {
			String val = config.getString("Dic." + key);
			set_Dic(key,val);
		}
	}

	//TO CONFIG
	public static void save_Dic() {
		Dictionary_Config dc = new Dictionary_Config();
		FileConfiguration config = dc.getConfig();
		config.set("Dic", null);
		for(String key : dic.keySet()) {
			config.set("Dic." + key, get_Dic(key));
		}
		dc.save_Yaml();
	}

	public static String replace_Wrong_Words(String original) {
		for(String key : get_Dic_Keys()) {

			original = original.replace(key, get_Dic(key));
		}
		return original;
	}



}
