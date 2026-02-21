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
		float m = p.getRank().getMultiplier();
		if (id == 0) return formatMultiplier(m);
		Guild guild = getGuild(id);
		if (guild == null) return formatMultiplier(m);
		float add = guild.getPerk(Guild.Perk.COINS_MULT) * 0.1F;
		return formatMultiplier(m + add);
	}

	public static String getDisplayName(Player player) {
		String prefix = prefixes.getOrDefault(player.getName().toLowerCase(), null);
		if (prefix == null) prefix = player.getRank() != Player.Rank.PLAYER ? player.getRank() + " " : "§7";
		else prefix += " ";
		if (!prefix.contains("§")) prefix = player.getRank().getStyle() + prefix;
		return prefix + getStyledName(player);
	}

	public static String getSimpleName(String s) {
		Player p = getPlayer(s);
		return getStyledName(p);
	}

	private static String getStyledName(Player player) {
		if (player == null) return "";
		if (!player.getCustomColors().isEmpty()) {
			if (player.getCustomColors().size() == 1) {
				return hexColor(player.getCustomColors().get(0)) + player.getName() + "§r";
			}
			return gradientByStops(player.getName(), player.getCustomColors()) + "§r";
		}
		return (player.getRank() == Player.Rank.PLAYER ? "§e" : player.getRank().getColor()) + player.getName();
	}

	private static String gradientByStops(String text, java.util.List<Integer> stops) {
		if (text == null || text.isEmpty() || stops == null || stops.isEmpty()) return text == null ? "" : text;
		if (stops.size() == 1) return hexColor(stops.get(0)) + text;

		StringBuilder builder = new StringBuilder(text.length() * 14);
		for (int i = 0; i < text.length(); i++) {
			float t = text.length() == 1 ? 0 : (float) i / (text.length() - 1);
			float scaled = t * (stops.size() - 1);
			int leftIndex = Math.min(stops.size() - 2, (int) Math.floor(scaled));
			float local = scaled - leftIndex;
			int c1 = stops.get(leftIndex);
			int c2 = stops.get(leftIndex + 1);
			int red = (int) (((c1 >> 16) & 0xFF) * (1 - local) + ((c2 >> 16) & 0xFF) * local);
			int green = (int) (((c1 >> 8) & 0xFF) * (1 - local) + ((c2 >> 8) & 0xFF) * local);
			int blue = (int) ((c1 & 0xFF) * (1 - local) + (c2 & 0xFF) * local);
			appendHexColor(builder, red, green, blue);
			builder.append(text.charAt(i));
		}
		return builder.toString();
	}

	private static String hexColor(int rgb) {
		StringBuilder sb = new StringBuilder(14);
		appendHexColor(sb, (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
		return sb.toString();
	}

	private static String formatMultiplier(float multiplier) {
		if (Math.abs(multiplier - Math.round(multiplier)) < 0.0001f) return String.valueOf((int) multiplier);
		return String.valueOf(multiplier);
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
