package jotan.jpchat.datamanage.yaml;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import jotan.jpchat.JPChat;

public class Dictionary_Config {

	//必要なパート開始
	private File path_to_yaml = new File(JPChat.getInstance().getDataFolder() + "/Dictionary.yml");
	private FileConfiguration Config = YamlConfiguration.loadConfiguration(path_to_yaml);

	public Dictionary_Config() {
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
