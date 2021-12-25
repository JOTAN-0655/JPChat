package jotan.jpchat.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Group_Manager {

	private static List<Chat_Group> groups = new ArrayList<Chat_Group>();

	public static List<Chat_Group> getGroups() {
		return groups;
	}

	public static void setGroups(List<Chat_Group> groups) {
		Group_Manager.groups = groups;
	}

	public static class Chat_Group{
		List<UUID> players = new ArrayList<UUID>();
		String group_name = "";
		public List<UUID> getPlayers() {
			return players;
		}
		public void setPlayers(List<UUID> players) {
			this.players = players;
		}
		public String getGroup_name() {
			return group_name;
		}
		public void setGroup_name(String group_name) {
			this.group_name = group_name;
		}

		String nick_name = "";
		boolean nick_name_changeable = true;

		UUID owner;
		public UUID getOwner() {
			return owner;
		}
		public void setOwner(UUID owner) {
			this.owner = owner;
		}

		boolean disbandable = true;
		public boolean isDisbandable() {
			return disbandable;
		}
		public void setDisbandable(boolean disbandable) {
			this.disbandable = disbandable;
		}

		boolean joinable = true;
		public boolean isJoinable() {
			return joinable;
		}
		public void setJoinable(boolean joinable) {
			this.joinable = joinable;
		}

		boolean separate_global = true;
		public boolean isSeparate_global() {
			return separate_global;
		}
		public void setSeparate_global(boolean separate_global) {
			this.separate_global = separate_global;
		}

		boolean leaveable = true;
		public boolean isLeaveable() {
			return leaveable;
		}
		public void setLeaveable(boolean leaveable) {
			this.leaveable = leaveable;
		}
		public String getNick_name() {
			return nick_name;
		}
		public void setNick_name(String nick_name) {
			this.nick_name = nick_name;
		}
		public boolean isNick_name_changeable() {
			return nick_name_changeable;
		}
		public void setNick_name_changeable(boolean nick_name_changeable) {
			this.nick_name_changeable = nick_name_changeable;
		}
	}

	public static Chat_Group get_Group(String name) {
		for(Chat_Group cg : groups) {
			if(cg.getGroup_name().equals(name)) return cg;
		}
		return null;
	}

	public static Chat_Group get_Group(Player p) {
		for(Chat_Group cg : groups) {
			if(cg.getPlayers().contains(p.getUniqueId())) return cg;
		}
		return null;
	}

	public static boolean remove_Group(Chat_Group g) {
		return groups.remove(g);
	}

	public static Chat_Group new_Group(String name,List<Player> players) {
		Chat_Group cg = new Chat_Group();
		cg.setGroup_name(name);
		List<UUID> uuid = new ArrayList<UUID>();
		for(Player p : players) {
			uuid.add(p.getUniqueId());
		}
		cg.setPlayers(uuid);
		return cg;
	}
}
