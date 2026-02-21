package evidence.vimeworld;

import evidence.render.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final int id;
    private final String name;
    private final int level;
    private final Rank rank;
    private final int guildID;
	private final String tag;
	private final List<Integer> customColors;

	Player(JSONObject user) {
        try {
            this.id = user.getInt("id");
            this.name = user.getString("username");
            this.level = user.getInt("level");
			this.rank = Rank.fromApi(user.optString("rank", "PLAYER"));
            List<Integer> customColors = new ArrayList<>();
            JSONArray customColorsJson = user.optJSONArray("customColors");
            if (customColorsJson != null) {
                for (int i = 0; i < customColorsJson.length(); i++) {
                    String hex = customColorsJson.optString(i, "").trim();
                    if (hex.isEmpty()) continue;
                    try {
                        customColors.add(Integer.parseInt(hex, 16));
                    } catch (NumberFormatException ignored) {}
                }
            }

            int id = 0;
            String tag = null;
            try {
				JSONObject g = user.getJSONObject("guild");
				id = g.getInt("id");
				tag = g.getString("tag");
                if (tag != null) tag = g.getString("color").replace('&', '§') + tag;
            } catch (JSONException ignored) {}
            guildID = id;
            this.tag = tag;
            this.customColors = Collections.unmodifiableList(customColors);
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public enum Rank {
        PLAYER("Игрок", "§7", 1, Color.GRAY),
        VIP("VIP", "§a[V]", 2, Color.GREEN),
        PREMIUM("Premium", "§b[P]", 3, Color.AQUA),
        HOLY("Holy", "§6[H]", 4, Color.GOLD),
        IMMORTAL("Immortal", "§d[I]", 5, Color.PINK),
        DIVINE("Divine", "§5[D]", 5.5f, Color.PINK),
        THANE("Thane", "§c[T]", 6, Color.RED),
        ELITE("Elite", "§6[E]", 6.5f, Color.GOLD),
        ETERNAL("Eternal", "§3[ET]", 7, Color.CYAN),
        CELESTIAL("Celestial", "§b[C]", 8.5f, Color.AQUA),
        ABSOLUTE("Absolute", "§4[A]", 10, Color.BLOOD),
        IMPERIAL("Imperial", "§d[I]", 11, Color.PINK),
        ULTIMATE("Ultimate", "§6[U]", 12, Color.GOLD),
        BUILDER("Билдер", "§2[Билдер]", 4, Color.LEAF),
        MAPLEAD("Главный билдер", "§2[Гл. билдер]", 4, Color.LEAF),
        YOUTUBE("YouTube", "§c[You§fTube§c]", 4, Color.RED),
        DEV("Разработчик", "§3[Dev]", 4, Color.CYAN),
        ORGANIZER("Организатор", "§3[Организатор]", 4, Color.CYAN),
        MODER("Модератор", "§9[Модер]", 4, Color.BLUE),
        WARDEN("Проверенный модератор", "§9[Модер]", 4, Color.BLUE),
        CHIEF("Главный модератор", "§9[Гл. модер]", 4, Color.BLUE),
        ADMIN("Главный админ", "§3§l[Гл. админ]", 4, Color.CYAN, true),
        UNKNOWN("Неизвестно", "§7", 1, Color.GRAY);

        private final String title, prefix;
        private final float multiplier;
		private final Color color;
		private final boolean bold;

		Rank(String title, String prefix, float multiplier, Color color) {
			this(title, prefix, multiplier, color, false);
		}
		Rank(String title, String prefix, float multiplier, Color color, boolean bold) {
            this.title = title;
            this.multiplier = multiplier;
            this.prefix = prefix;
            this.color = color;
            this.bold = bold;
        }

		public boolean isBold() {
			return bold;
		}

		public float getMultiplier() {
			return multiplier;
		}

		@Override
		public String toString() {
			return prefix;
		}

		public Color getColor() {
        	return color;
		}

		public String getStyle() {
			return color.toString() + (isBold() ? "§l" : "");
		}

		public boolean isDonater() {
			return this == VIP || this == PREMIUM || this == HOLY || this == IMMORTAL
					|| this == DIVINE || this == THANE || this == ELITE || this == ETERNAL
					|| this == CELESTIAL || this == ABSOLUTE || this == IMPERIAL || this == ULTIMATE;
		}

		public static Rank fromApi(String rankName) {
			for (Rank rank : values()) {
				if (rank.name().equalsIgnoreCase(rankName)) return rank;
			}
			return UNKNOWN;
		}
	}

    public evidence.vimeworld.Session getSession() {
        try {
            JSONObject online = new JSONObject(API.readRequest("https://api.vimeworld.com/user/" + id + "/session"));
            return new evidence.vimeworld.Session(online.getJSONObject("online"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getId() {
        return id;
    }

	public String getTag() {
		return tag == null ? "" : "§7<§f" + tag + "§7> ";
	}

	public int getGuildID() {
        return guildID;
    }

    public Rank getRank() {
        return rank;
    }

	public List<Integer> getCustomColors() {
		return customColors;
	}

    @Override
    public String toString() {
        return rank.prefix + (rank == Rank.PLAYER ? "" : " ") + name + " [id" + id + ", level " + level + ", guild " + guildID + "]";
    }
}
