package utils.parsing;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonReader {
    public static String readFromJson(String property, String json) {
        String regex = String.format(".*%s\\\"\\:\\\"*([a-zA-Z0-9._ ]+)\\\"*.*", property);
        String arrayRegex = String.format(".*%s\\\"\\:\\\"*\\[(.*)\\].*", property);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        if (matcher.matches()) return matcher.group(1);
        pattern = Pattern.compile(arrayRegex);
        matcher = pattern.matcher(json);
        if (matcher.matches()) return matcher.group(1);
        return "";
    }

    public static String[] readFromArray(String json) {
        return json.split("},\\{");
    }

    public static <K, V> String mapToJSON(Map<K, V> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (K key : map.keySet()) {
            sb.append(pairToJSON(key, map.get(key))).append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private static <K, V> String pairToJSON(K key, V value) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
        return sb.toString();
    }
}
