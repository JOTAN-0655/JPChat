package jotan.jpchat.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ConversionJP {

	public static String convertrome(String content) {
		String step1 = conv1(content);
		String step2 = convByGoogleIME(step1);
		return step2;
	}
	/**
     * ローマ字をかな文字へ変換する
     * @param org 変換元文字列
     * @return 変換後の文字列
     */
    public static String conv1(String org) {

        String last = "";
        StringBuilder line = new StringBuilder();

        for ( int i=0; i<org.length(); i++ ) {
            String tmp = org.substring(i,i+1);

            if ( tmp.equals("a") ) {
                line.append( getKanaFromTable(last, 0) );
                last = "";
            } else if ( tmp.equals("i") ) {
                line.append( getKanaFromTable(last, 1) );
                last = "";
            } else if ( tmp.equals("u") ) {
                line.append( getKanaFromTable(last, 2) );
                last = "";
            } else if ( tmp.equals("e") ) {
                line.append( getKanaFromTable(last, 3) );
                last = "";
            } else if ( tmp.equals("o") ) {
                line.append( getKanaFromTable(last, 4) );
                last = "";
            } else {
                if ( last.equals("n") && !(tmp.equals("y")) ) {
                    line.append("ん");
                    last = "";
                    if ( tmp.equals("n") ) {
                        continue;
                    }
                }
                if ( Character.isLetter(tmp.charAt(0)) ) {
                    if ( Character.isUpperCase(tmp.charAt(0)) ) {
                        line.append(last + tmp);
                        last = "";
                    } else if ( last.equals(tmp) ) {
                        line.append("っ");
                        last = tmp;
                    } else {
                        last = last + tmp;
                    }
                } else {
                    if ( tmp.equals("-") ) {
                        line.append(last + "ー");
                        last = "";
                    } else if ( tmp.equals(".") ) {
                        line.append(last + "。");
                        last = "";
                    } else if ( tmp.equals(",") ) {
                        line.append(last + "、");
                        last = "";
                    } else if ( tmp.equals("?") ) {
                        line.append(last + "？");
                        last = "";
                    } else if ( tmp.equals("!") ) {
                        line.append(last + "！");
                        last = "";
                    } else if ( tmp.equals("[") ) {
                        line.append(last + "「");
                        last = "";
                    } else if ( tmp.equals("]") ) {
                        line.append(last + "」");
                        last = "";
                    } else if ( tmp.equals("<") ) {
                        line.append(last + "＜");
                        last = "";
                    } else if ( tmp.equals(">") ) {
                        line.append(last + "＞");
                        last = "";
                    } else if ( tmp.equals("&") ) {
                        line.append(last + "＆");
                        last = "";
                    } else if ( tmp.equals("\"") ) {
                        line.append(last + "”");
                        last = "";
                    } else if ( tmp.equals("(") || tmp.equals(")") ) {
                        line.append(last);
                        last = "";
                    } else {
                        line.append(last + tmp);
                        last = "";
                    }
                }
            }
        }
        line.append(last);

        return line.toString();
    }
    private static final String SOCIAL_IME_URL =
	        "http://www.social-ime.com/api/?string=";
    private static final String GOOGLE_IME_URL =
    		"http://www.google.com/transliterate?langpair=ja-Hira|ja&text=";

    public static String convByGoogleIME(String org) {
        return conv(org, true);
    }

 // 変換の実行
    private static String conv(String org, boolean isGoogleIME) {

        if ( org.length() == 0 ) {
            return "";
        }

        HttpURLConnection urlconn = null;
        BufferedReader reader = null;
        try {
            String baseurl;
            String encode;
            if ( isGoogleIME ) {
                baseurl = GOOGLE_IME_URL + URLEncoder.encode(org , "UTF-8");
                encode = "UTF-8";
            } else {
                baseurl = SOCIAL_IME_URL + URLEncoder.encode(org , "UTF-8");
                encode = "EUC_JP";
            }
            URL url = new URL(baseurl);

            urlconn = (HttpURLConnection)url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.connect();

            reader = new BufferedReader(
                    new InputStreamReader(urlconn.getInputStream(), encode));
            String line = "";
            StringBuilder result = new StringBuilder();
            while ( (line = reader.readLine()) != null ) {
                if ( isGoogleIME ) {
                    result.append(parseGoogleIMEResult(line));
                } else {
                    result.append(pickFirstElement(line));
                }
            }

            return result.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( urlconn != null ) {
                urlconn.disconnect();
            }
            if ( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) { // do nothing.
                }
            }
        }

        return "";
    }

    private static String pickFirstElement(String org) {
        int index = org.indexOf("\t");
        if ( index == -1 ) {
            return org;
        } else {
            return org.substring(0, index);
        }
    }

    private static String parseGoogleIMEResult(String result) {
        StringBuilder buf = new StringBuilder();
        int level = 0;
        int index = 0;
        while ( index < result.length() ) {
            if ( level < 3 ) {
                int nextStart = result.indexOf("[", index);
                int nextEnd = result.indexOf("]", index);
                if ( nextStart == -1 ) {
                    return buf.toString();
                } else {
                    if ( nextStart < nextEnd ) {
                        level++;
                        index = nextStart+1;
                    } else {
                        level--;
                        index = nextEnd+1;
                    }
                }
            } else {
                int start = result.indexOf("\"", index);
                int end = result.indexOf("\"", start+1);
                if ( start == -1 || end == -1 ) {
                    return buf.toString();
                }
                buf.append(result.substring(start+1, end));
                int next = result.indexOf("]", end);
                if ( next == -1 ) {
                    return buf.toString();
                } else {
                    level--;
                    index = next+1;
                }
            }
        }
        return buf.toString();
    }

    private static final HashMap<String, String[]> TABLE;
    static {
        TABLE = new HashMap<String, String[]>();
        TABLE.put(  "", new String[]{"あ","い","う","え","お"});
        TABLE.put( "k", new String[]{"か","き","く","け","こ"});
        TABLE.put( "s", new String[]{"さ","し","す","せ","そ"});
        TABLE.put( "t", new String[]{"た","ち","つ","て","と"});
        TABLE.put( "n", new String[]{"な","に","ぬ","ね","の"});
        TABLE.put( "h", new String[]{"は","ひ","ふ","へ","ほ"});
        TABLE.put( "m", new String[]{"ま","み","む","め","も"});
        TABLE.put( "y", new String[]{"や","い","ゆ","いぇ","よ"});
        TABLE.put( "r", new String[]{"ら","り","る","れ","ろ"});
        TABLE.put( "w", new String[]{"わ","うぃ","う","うぇ","を"});
        TABLE.put( "g", new String[]{"が","ぎ","ぐ","げ","ご"});
        TABLE.put( "z", new String[]{"ざ","じ","ず","ぜ","ぞ"});
        TABLE.put( "j", new String[]{"じゃ","じ","じゅ","じぇ","じょ"});
        TABLE.put( "d", new String[]{"だ","ぢ","づ","で","ど"});
        TABLE.put( "b", new String[]{"ば","び","ぶ","べ","ぼ"});
        TABLE.put( "p", new String[]{"ぱ","ぴ","ぷ","ぺ","ぽ"});
        TABLE.put("gy", new String[]{"ぎゃ","ぎぃ","ぎゅ","ぎぇ","ぎょ"});
        TABLE.put("gw", new String[]{"ぐぁ","ぐぃ","ぐぅ","ぐぇ","ぐぉ"});
        TABLE.put("zy", new String[]{"じゃ","じぃ","じゅ","じぇ","じょ"});
        TABLE.put("jy", new String[]{"じゃ","じぃ","じゅ","じぇ","じょ"});
        TABLE.put("dy", new String[]{"ぢゃ","ぢぃ","ぢゅ","ぢぇ","ぢょ"});
        TABLE.put("dh", new String[]{"でゃ","でぃ","でゅ","でぇ","でょ"});
        TABLE.put("dw", new String[]{"どぁ","どぃ","どぅ","どぇ","どぉ"});
        TABLE.put("by", new String[]{"びゃ","びぃ","びゅ","びぇ","びょ"});
        TABLE.put("py", new String[]{"ぴゃ","ぴぃ","ぴゅ","ぴぇ","ぴょ"});
        TABLE.put( "v", new String[]{"ヴぁ","ヴぃ","ヴ","ヴぇ","ヴぉ"});
        TABLE.put("vy", new String[]{"ヴゃ","ヴぃ","ヴゅ","ヴぇ","ヴょ"});
        TABLE.put("sh", new String[]{"しゃ","し","しゅ","しぇ","しょ"});
        TABLE.put("sy", new String[]{"しゃ","し","しゅ","しぇ","しょ"});
        TABLE.put( "c", new String[]{"か","し","く","せ","こ"});
        TABLE.put("ch", new String[]{"ちゃ","ち","ちゅ","ちぇ","ちょ"});
        TABLE.put("cy", new String[]{"ちゃ","ち","ちゅ","ちぇ","ちょ"});
        TABLE.put( "f", new String[]{"ふぁ","ふぃ","ふ","ふぇ","ふぉ"});
        TABLE.put("fy", new String[]{"ふゃ","ふぃ","ふゅ","ふぇ","ふょ"});
        TABLE.put("fw", new String[]{"ふぁ","ふぃ","ふ","ふぇ","ふぉ"});
        TABLE.put( "q", new String[]{"くぁ","くぃ","く","くぇ","くぉ"});
        TABLE.put("ky", new String[]{"きゃ","きぃ","きゅ","きぇ","きょ"});
        TABLE.put("kw", new String[]{"くぁ","くぃ","く","くぇ","くぉ"});
        TABLE.put("ty", new String[]{"ちゃ","ちぃ","ちゅ","ちぇ","ちょ"});
        TABLE.put("ts", new String[]{"つぁ","つぃ","つ","つぇ","つぉ"});
        TABLE.put("th", new String[]{"てゃ","てぃ","てゅ","てぇ","てょ"});
        TABLE.put("tw", new String[]{"とぁ","とぃ","とぅ","とぇ","とぉ"});
        TABLE.put("ny", new String[]{"にゃ","にぃ","にゅ","にぇ","にょ"});
        TABLE.put("hy", new String[]{"ひゃ","ひぃ","ひゅ","ひぇ","ひょ"});
        TABLE.put("my", new String[]{"みゃ","みぃ","みゅ","みぇ","みょ"});
        TABLE.put("ry", new String[]{"りゃ","りぃ","りゅ","りぇ","りょ"});
        TABLE.put( "l", new String[]{"ぁ","ぃ","ぅ","ぇ","ぉ"});
        TABLE.put( "x", new String[]{"ぁ","ぃ","ぅ","ぇ","ぉ"});
        TABLE.put("ly", new String[]{"ゃ","ぃ","ゅ","ぇ","ょ"});
        TABLE.put("lt", new String[]{"た","ち","っ","て","と"});
        TABLE.put("lk", new String[]{"ヵ","き","く","ヶ","こ"});
        TABLE.put("xy", new String[]{"ゃ","ぃ","ゅ","ぇ","ょ"});
        TABLE.put("xt", new String[]{"た","ち","っ","て","と"});
        TABLE.put("xk", new String[]{"ヵ","き","く","ヶ","こ"});
        TABLE.put("wy", new String[]{"わ","ゐ","う","ゑ","を"});
        TABLE.put("wh", new String[]{"うぁ","うぃ","う","うぇ","うぉ"});
    };

    private static String getKanaFromTable(String s, int n) {

        if ( TABLE.containsKey(s) ) {
            return TABLE.get(s)[n];
        }
        return s + TABLE.get("")[n];
    }
}
