package evidence.vimeworld;

import java.util.HashMap;
import java.util.Map;

public class Vime {

	public static final Map<String, Player> playercache = new HashMap<>();
	private static final Map<Integer, Guild> guildcache = new HashMap<>();
	public static Map<String, String> prefixes = new HashMap<>();

	public static Player getPlayer(String name) {
		if (name.contains("[")) {
			String[] ss = name.split(" ");
			name = ss[1];
			prefixes.put(name.toLowerCase(), ss[0]);
		}
		return playercache.computeIfAbsent(name.toLowerCase(), API::getPlayer);
	}

	public static Guild getGuild(int id) {
		return guildcache.computeIfAbsent(id, API::getGuild);
	}

	public static String getPlayerMultiplier(Player p) {
		int id = p.getGuildID();
		int m = p.getRank().getMultiplier();
		if (id == 0) return m + "";
		Guild guild = getGuild(id);
		if (guild == null) return m + "";
		float add = guild.getPerk(Guild.Perk.COINS_MULT) * 0.1F;
		return add == 0 || add == 1 ? String.valueOf((int) (m + add)) : String.valueOf(m + add);
	}

	public static String getDisplayName(Player player) {
		String prefix = prefixes.getOrDefault(player.getName().toLowerCase(), null);
		if (prefix == null) {
			if (player.getRank() != Player.Rank.PLAYER) {
				prefix = player.getRank().toString();
				if (player.getRank().isDonater()) {
					prefix = gradient(prefix, 0x55FF55, 0xAA00AA) + "§r";
				}
				prefix += " ";
			} else {
				prefix = "§7";
			}
		}
		else prefix += " ";
		if (!prefix.contains("§")) prefix = player.getRank().getStyle() + prefix;
		return prefix + player.getName();
	}

	public static String getSimpleName(String s) {
		Player p = getPlayer(s);
		if (p.getRank().isDonater()) {
			return gradient(p.getName(), 0x55FF55, 0xAA00AA) + "§r";
		}
		return (p.getRank() == Player.Rank.PLAYER ? "§e" : p.getRank().getColor()) + p.getName();
	}

	private static String gradient(String text, int rgbStart, int rgbEnd) {
		if (text == null || text.isEmpty()) return "";
		int chars = text.length();
		StringBuilder builder = new StringBuilder(chars * 14);
		for (int i = 0; i < chars; i++) {
			float t = chars == 1 ? 0 : (float) i / (chars - 1);
			int red = (int) (((rgbStart >> 16) & 0xFF) * (1 - t) + ((rgbEnd >> 16) & 0xFF) * t);
			int green = (int) (((rgbStart >> 8) & 0xFF) * (1 - t) + ((rgbEnd >> 8) & 0xFF) * t);
			int blue = (int) ((rgbStart & 0xFF) * (1 - t) + (rgbEnd & 0xFF) * t);
			appendHexColor(builder, red, green, blue);
			builder.append(text.charAt(i));
		}
		return builder.toString();
	}

	private static void appendHexColor(StringBuilder builder, int red, int green, int blue) {
		builder.append('§').append('x');
		appendHexDigit(builder, (red >> 4) & 0xF);
		appendHexDigit(builder, red & 0xF);
		appendHexDigit(builder, (green >> 4) & 0xF);
		appendHexDigit(builder, green & 0xF);
		appendHexDigit(builder, (blue >> 4) & 0xF);
		appendHexDigit(builder, blue & 0xF);
	}

	private static void appendHexDigit(StringBuilder builder, int digit) {
		builder.append('§').append(Character.toLowerCase(Character.forDigit(digit, 16)));
	}

}
