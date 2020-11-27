package utils;

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
}
