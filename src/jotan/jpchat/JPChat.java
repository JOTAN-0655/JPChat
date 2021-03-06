package jotan.jpchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jotan.jpchat.Group.Group_Manager;
import jotan.jpchat.Group.Group_Manager.Chat_Group;
import jotan.jpchat.convert.ConversionJP;
import jotan.jpchat.convert.Translate;
import jotan.jpchat.convert.Translate.Locale_ID_Name;
import jotan.jpchat.datamanage.Dictionary_Manager;
import jotan.jpchat.datamanage.Player_Data_Manager;
import jotan.jpchat.datamanage.Player_Data_Manager.JPChat_Player_Data;

public class JPChat extends JavaPlugin implements Listener{
	static JPChat plugin;
	public static JPChat getInstance() {
		return plugin;
	}

	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getLogger().info("Enabling JapanesePlugin V1.18-1.0.0");
		this.getLogger().info("Using source code LUNACHAT");
		this.getLogger().info("Plugin was made by JOTAN");
		this.getDataFolder().mkdir();
		plugin = this;
		plugin.saveDefaultConfig();
		load_System();
		load_config();


	}

	public void load_System() {
		ConfigurationSerialization.registerClass(JPChat_Player_Data.class);
	}

	public void load_config(){
		plugin.reloadConfig();
		Dictionary_Manager.load_Dic();
	    Player_Data_Manager.load_all();
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Disabled JPChat");
		save_config();
	}

	public void save_config() {
		Dictionary_Manager.save_Dic();
		Player_Data_Manager.save_all();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(true);
		String content = e.getMessage();
		String romaji_cnv = ConversionJP.convertrome(content);
		String romaji_result = Dictionary_Manager.replace_Wrong_Words(romaji_cnv);
		//WHEN START WITH PERIOD OR HTTP
		if(content.startsWith(".") || content.startsWith("http")) {
			SendMessage.send_Message(p, content, null);
			return;
		}

		//WHEN START WITH @
		if(content.startsWith("@")) {
			int start = content.indexOf("@");
			String mention_to = "";
			for(int i = 0; i < content.length()-start;i++) {
				if(content.charAt(i) == ' ') {
					break;
				}else {
					mention_to += content.charAt(i);
				}
			}
			if(p.hasPermission("jpchat.mention")) {
				SendMessage.send_Message(p, content.replace(mention_to, ""), content);
			}else {
				JPChat.sendmessage(p, ChatColor.RED + "????????????????????????????????????????????????????????????????????????");
				SendMessage.send_Message(p, content.replace(mention_to, ""), content);
			}
			return;
		}


		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);


		if(jppd.getTalk_language().equals(Locale_ID_Name.Japanese_Roman) && jppd.getTranslate_language().equals(Locale_ID_Name.Japanese)) {

			SendMessage.send_Message(p, romaji_result, content);
			return;
		}

		if(jppd.getTalk_language().equals(Locale_ID_Name.Japanese_Roman))
			content = romaji_result;



		if(jppd.getTalk_language().getString().equals(jppd.getTranslate_language().getString())) {
			SendMessage.send_Message(p, content, null);
		}else {
			try {
				String translate_result = Translate.translate(content, jppd.getTranslate_language(), jppd.getTalk_language());
				SendMessage.send_Message(p, translate_result, content);
			} catch (IOException e1) {
				// TODO ????????????????????? catch ????????????
				e1.printStackTrace();
				SendMessage.send_Message(p, content, "ERROR OCCURRED AT TRANSLATE");
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);

		if(getConfig().getBoolean("Login_Message")) {
			if(!p.hasPlayedBefore()) {
				Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "????????????????????????????????????");
			}
			String name = jppd.getNick_name();
			e.setJoinMessage(jppd.getColor() + name + "????????????????????????");
		}


	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);

		if(getConfig().getBoolean("Logout_Message")) {
			e.setQuitMessage(jppd.getColor() + jppd.getNick_name() + "????????????????????????");
		}


		Chat_Group cg = Group_Manager.get_Group(p);
		if(cg != null) {
			for(UUID uuid : cg.getPlayers()) {
				Player cg_player = Bukkit.getPlayer(uuid);
				if(cg_player == null) continue;
				if(cg_player.equals(p)) continue;
				JPChat.sendmessage(cg_player, p.getName() + "???????????????????????????????????????");
			}
			cg.getPlayers().remove(p.getUniqueId());
		}

	}

	public static void sendmessage(Player p , String x) {
		p.sendMessage("[" + ChatColor.WHITE + "JP" + ChatColor.RED + "PLUGIN" + ChatColor.RESET + "]" + x);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		if(sender instanceof Player == false) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		if(command.getName().equalsIgnoreCase("jpchat")) {
			if(args.length == 1) {
				if ("translate".startsWith(args[0].toLowerCase())) list.add("translate");
				if ("talk".startsWith(args[0].toLowerCase())) list.add("talk");
				if ("list".startsWith(args[0].toLowerCase())) list.add("list");
				if ("name".startsWith(args[0].toLowerCase())) list.add("name");
				if ("color".startsWith(args[0].toLowerCase())) list.add("color");
				if ("reload".startsWith(args[0].toLowerCase())) list.add("reload");
				if ("dic".startsWith(args[0].toLowerCase())) list.add("dic");
				if ("group".startsWith(args[0].toLowerCase())) list.add("group");
			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("translate") || args[0].equalsIgnoreCase("talk")) {
					for(Locale_ID_Name lin : Locale_ID_Name.values()) {
						if(lin.toString().startsWith(args[1])) list.add(lin.toString());
					}
				}else if(args[0].equalsIgnoreCase("dic")) {
					if("add".startsWith(args[1].toLowerCase())) list.add("add");
					if("remove".startsWith(args[1].toLowerCase())) list.add("remove");
					if("list".startsWith(args[1].toLowerCase())) list.add("list");
				}else if(args[0].equalsIgnoreCase("color")) {
					for(ChatColor cc : ChatColor.values()) {
						if(cc.name().startsWith(args[1])) list.add(cc.name());
					}
				}else if(args[0].equalsIgnoreCase("group")) {
					if("add".startsWith(args[1].toLowerCase())) list.add("add");
					if("join".startsWith(args[1].toLowerCase())) list.add("join");
					if("leave".startsWith(args[1].toLowerCase())) list.add("leave");
					if("disband".startsWith(args[1].toLowerCase())) list.add("disband");
					if("set".startsWith(args[1].toLowerCase())) list.add("set");
				}
			}else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("dic")) {
					if(args[1].equalsIgnoreCase("add")) {
						list.add("[???]");
					}else if(args[1].equalsIgnoreCase("remove")) {
						for(String s : Dictionary_Manager.get_Dic_Keys()) {
							if(s.startsWith(args[2].toLowerCase())) {
								list.add(s);
							}
						}
					}
				}else if(args[0].equalsIgnoreCase("group")) {
					if(args[1].equalsIgnoreCase("add")) {
						list.add("[GROUP NAME]");
					}else if(args[1].equalsIgnoreCase("join")) {
						for(Chat_Group cg : Group_Manager.getGroups()) {
							if(cg.getGroup_name().startsWith(args[2])) list.add(cg.getGroup_name());
						}
					}else if(args[1].equalsIgnoreCase("disband")) {
						for(Chat_Group cg : Group_Manager.getGroups()) {
							if(cg.getGroup_name().startsWith(args[2])) list.add(cg.getGroup_name());
						}
					}else if(args[1].equalsIgnoreCase("set")) {
						for(Chat_Group cg : Group_Manager.getGroups()) {
							if(cg.getGroup_name().startsWith(args[2])) list.add(cg.getGroup_name());
						}
					}
				}
			}else if(args.length == 4) {
				if(args[0].equalsIgnoreCase("dic")) {
					if(args[1].equalsIgnoreCase("add")) {
						list.add("[???]");
					}

				}else if(args[0].equalsIgnoreCase("group")) {
					if(args[1].equalsIgnoreCase("set")) {
						if("nickname".startsWith(args[3].toLowerCase())) list.add("nickname");
					}
				}
			}

		}
		return list;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(cmd.getName().equalsIgnoreCase("jpchat")) {
				if(args.length == 0) {
					sendmessage(p,ChatColor.RED + "???????????????????????????");
					return true;
				}
				if(args[0].equalsIgnoreCase("translate")) {
					if(args.length == 1) {
						sendmessage((Player) sender,"????????????????????????");
						sendmessage((Player) sender,"/jpchat translate [??????]");
						return true;
					}

					JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
					try {
						Locale_ID_Name lin = Locale_ID_Name.valueOf(args[1]);
						jppd.setTranslate_language(lin);
						sendmessage((Player) sender,"???????????????" + args[1] + "????????????????????????");
					}catch(Exception e) {
						sendmessage(p,ChatColor.RED + args[1] + "????????????????????????");
					}
				}else if(args[0].equalsIgnoreCase("talk")) {
					if(args.length == 1) {
						sendmessage((Player) sender,"????????????????????????");
						sendmessage((Player) sender,"/jpchat talk [??????]");
						return true;
					}

					JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
					try {
						Locale_ID_Name lin = Locale_ID_Name.valueOf(args[1]);
						jppd.setTalk_language(lin);
						sendmessage((Player) sender,"???????????????????????????" + args[1] + "????????????????????????");
					}catch(Exception e) {
						sendmessage(p,ChatColor.RED + args[1] + "????????????????????????");
					}
				}else if(args[0].equalsIgnoreCase("color")){
					if(args.length == 1) {
						sendmessage((Player) sender,"????????????????????????");
						sendmessage((Player) sender,"/jpchat talk [COLOR]");
						return true;
					}

					JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
					try {
						ChatColor color = ChatColor.valueOf(args[1]);
						jppd.setColor(color);
						sendmessage((Player) sender,"??????" + color.toString() + color.name() + ChatColor.RESET + "????????????????????????");
					}catch(Exception e) {
						sendmessage(p,ChatColor.RED + args[1] + "????????????????????????");
					}
				}
				else if(args[0].equalsIgnoreCase("list")) {
					sendmessage((Player) sender,"?????????????????????");
					for(Locale_ID_Name lin : Locale_ID_Name.values()) {
						sendmessage((Player) sender,lin.toString());
					}
				}else if(args[0].equalsIgnoreCase("name")) {
					if(args.length == 1) {
						JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
						jppd.setNick_name(p.getName());
						sendmessage((Player) sender,p.getName() + "????????????????????????");
					}else {
						int i = 0;
						String nick = "";
						for(i = 0;i < args.length - 1;i++) {
							nick += args[i+1] + " ";
						}
						if(nick.length() > 1) {
							nick = nick.substring(0, nick.length() - 1);
						}

						for(Player online : Bukkit.getOnlinePlayers()) {
							if(online.getName().equalsIgnoreCase(nick)) {
								sendmessage((Player) sender,ChatColor.RED + online.getName() + "?????????????????????????????????");
								return true;
							}
						}
						for(OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
							if(offline.getName().equalsIgnoreCase(nick)) {
								sendmessage((Player) sender,ChatColor.RED + offline.getName() + "?????????????????????????????????");
								return true;
							}
						}
						JPChat_Player_Data jppd = Player_Data_Manager.get_Player_Data(p);
						jppd.setNick_name(nick);
						sendmessage((Player) sender,"?????????????????????" + nick + "????????????????????????");
					}
				}else if(args[0].equalsIgnoreCase("dic")) {
					if(!p.hasPermission("jpchat.command.dictionary")) {
						sendmessage((Player) sender,ChatColor.RED + "????????????????????????????????????????????????????????????????????????");
					}
					String control = args[1];
					if(control.equalsIgnoreCase("add")) {
						if(args.length < 4) {
							sendmessage((Player) sender,ChatColor.RED + "????????? /jpchat dic [add] [?????????] [?????????]");
						}else {

							String wrong = args[2];
							String correct = args[3];
							Dictionary_Manager.set_Dic(wrong, correct);
							sendmessage((Player) sender,ChatColor.GREEN + "??????????????????????????????");
							sendmessage((Player) sender,wrong + "???" + correct);
						}

					}else if(control.equalsIgnoreCase("remove")) {
						if(args.length < 3) {
							sendmessage((Player) sender,ChatColor.RED + "????????? /jpchat dic [remove] [?????????]");
						}else {
							String wrong = args[2];
							boolean result = Dictionary_Manager.remove_Dic(wrong);
							if(result) {
								sendmessage((Player) sender,ChatColor.GREEN + "??????????????????????????????");
							}else {
								sendmessage((Player) sender,ChatColor.RED + wrong + "????????????????????????????????????????????????");
							}
						}
					}else if(control.equalsIgnoreCase("list")) {

						for(String wrong : Dictionary_Manager.get_Dic_Keys()) {
							String correct = Dictionary_Manager.get_Dic(wrong);
							sendmessage((Player) sender, "?????????: " + wrong + " > ?????????:" + correct);
						}
					}
				}else if(args[0].equalsIgnoreCase("reload")) {
					this.save_config();
					this.load_config();
					sendmessage((Player) sender, ChatColor.GREEN + "???????????????????????????");
				}else if(args[0].equalsIgnoreCase("group")) {
					if(args.length == 1) {
						JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
						return true;
					}
					if(args[1].equalsIgnoreCase("add")) {
						if(args.length < 3) {
							JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
							JPChat.sendmessage(p, ChatColor.RED + "/jpchat group add [NAME]");
							return true;
						}
						Chat_Group cg_joined = Group_Manager.get_Group(p);
						if(cg_joined!=null) {
							JPChat.sendmessage(p, ChatColor.RED + cg_joined.getGroup_name() + "???????????????????????????");
							return true;
						}
						String group_name = args[2];
						Chat_Group cg = Group_Manager.new_Group(group_name, new ArrayList<Player>());
						cg.setOwner(p.getUniqueId());
						Group_Manager.getGroups().add(cg);
						JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "????????????????????????");
					}else if(args[1].equalsIgnoreCase("join")) {
						if(args.length < 3) {
							JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
							JPChat.sendmessage(p, ChatColor.RED + "/jpchat group join [NAME]");
							return true;
						}
						Chat_Group cg_joined = Group_Manager.get_Group(p);
						if(cg_joined!=null) {
							JPChat.sendmessage(p, ChatColor.RED + cg_joined.getGroup_name() + "???????????????????????????");
							return true;
						}

						String group_name = args[2];
						Chat_Group cg = Group_Manager.get_Group(group_name);
						if(cg == null) {
							JPChat.sendmessage(p, ChatColor.RED + group_name + "??????????????????????????????????????????");
							return true;
						}
						if(!cg.isJoinable()) {
							JPChat.sendmessage(p, ChatColor.RED + group_name + "??????????????????????????????");
							return true;
						}
						for(UUID uuid : cg.getPlayers()) {
							Player cg_player = Bukkit.getPlayer(uuid);
							if(cg_player == null) continue;
							JPChat.sendmessage(cg_player, p.getName() + "???????????????????????????????????????");
						}
						cg.getPlayers().add(p.getUniqueId());
						JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "????????????????????????");

					}else if(args[1].equalsIgnoreCase("leave")) {
						Chat_Group cg = Group_Manager.get_Group(p);
						if(cg == null) {
							JPChat.sendmessage(p, ChatColor.RED + "??????????????????????????????????????????????????????????????????");
							return true;
						}
						if(!cg.isLeaveable()) {
							JPChat.sendmessage(p, ChatColor.RED + "?????????????????????????????????????????????");
							return true;
						}
						for(UUID uuid : cg.getPlayers()) {
							Player cg_player = Bukkit.getPlayer(uuid);
							if(cg_player == null) continue;
							if(cg_player.equals(p)) continue;
							JPChat.sendmessage(cg_player, p.getName() + "???????????????????????????????????????");
						}
						cg.getPlayers().remove(p.getUniqueId());
						JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "????????????????????????");
					}else if(args[1].equalsIgnoreCase("disband")) {
						if(args.length < 3) {
							JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
							JPChat.sendmessage(p, ChatColor.RED + "/jpchat group disband [NAME]");
							return true;
						}
						String group_name = args[2];
						Chat_Group cg = Group_Manager.get_Group(group_name);
						if(!cg.getOwner().equals(p.getUniqueId())) {
							JPChat.sendmessage(p, ChatColor.RED + "?????????????????????????????????????????????????????????");
							return true;
						}
						if(!cg.isDisbandable()) {
							JPChat.sendmessage(p, ChatColor.RED + "?????????????????????????????????????????????");
							return true;
						}

						for(UUID uuid : cg.getPlayers()) {
							Player cg_player = Bukkit.getPlayer(uuid);
							if(cg_player == null) continue;
							if(cg_player.equals(p)) continue;
							JPChat.sendmessage(cg_player, p.getName() + "???????????????????????????????????????");
						}

						cg.getPlayers().clear();
						Group_Manager.remove_Group(cg);
						JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "????????????????????????");
					}else if(args[1].equalsIgnoreCase("set")) {
						if(args.length < 4) {
							JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
							JPChat.sendmessage(p, ChatColor.RED + "/jpchat group set [group_name] [arg]");
							return true;
						}

						String group_name = args[2];
						Chat_Group cg = Group_Manager.get_Group(group_name);

						if(args[3].equalsIgnoreCase("nickname")) {
							if(args.length < 4) {
								JPChat.sendmessage(p, ChatColor.RED + "???????????????????????????");
								JPChat.sendmessage(p, ChatColor.RED + "/jpchat group set [group_name] nickname [name]");
								return true;
							}
							if(cg.isNick_name_changeable()) {
								if(args.length < 5) {
									cg.setNick_name("");
									JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "???????????????????????????????????????????????????????????????");
								}else {
									cg.setNick_name(args[4]);
									JPChat.sendmessage(p, ChatColor.GREEN + cg.getGroup_name() + "????????????????????????" + cg.getNick_name() + "????????????????????????");
								}
							}else {
								JPChat.sendmessage(p, ChatColor.RED + "??????????????????????????????????????????????????????????????????");
								return true;
							}
						}
					}
				}
			}
		}

		return true;
	}
}

