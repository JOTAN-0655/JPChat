package jotan.jpchat.convert;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jotan.jpchat.JPChat;

public class Translate {

	public static String translate(String message,Locale_ID_Name to,Locale_ID_Name from) throws IOException {
		String url = JPChat.getInstance().getConfig().getString("Google_Translate_URL")
				+ "?text=" + message + "&source=" + from.getString() + "&target=" + to.getString();
		JPChat.getInstance().getLogger().fine(url);
		Document document = Jsoup.connect(url).get();
		Elements pres = document.select("body");
		String xx = "";
		for (Element pre : pres) {
		    xx = xx + pre.text();
		}

		return xx;
	}

	public static enum Locale_ID_Name{
		Japanese("ja"),
		Japanese_Roman("ja"),
		English("en"),
		Russian("ru"),
		Chinese("zh_cn"),
		Germany("de"),
		Korea("ko"),
		Iceland("is");

		private final String text;
		Locale_ID_Name(String string) {
			// TODO 自動生成されたコンストラクター・スタブ
			this.text = string;
		}
		public String getString() {
	        return this.text;
	    }
	}

}
