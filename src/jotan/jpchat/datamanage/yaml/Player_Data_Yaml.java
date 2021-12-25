package jotan.jpchat.datamanage.yaml;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jotan.jpchat.JPChat;

public class Player_Data_Yaml {

	private File path_to_yaml = new File(JPChat.getInstance().getDataFolder() + "/Player_Data.yml");
	private FileConfiguration Config = YamlConfiguration.loadConfiguration(path_to_yaml);

	public Player_Data_Yaml() {
		if(!path_to_yaml.exists()) {
			save_Yaml();
		}
		reload_Yaml();
	}

	public void save_Yaml() {
		try {
			Config.save(path_to_yaml);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload_Yaml() {
		try {
			Config.load(path_to_yaml);
		}catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig() {
		return Config;
	}

}
