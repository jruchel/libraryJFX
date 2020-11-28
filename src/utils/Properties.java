package utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Properties {
    private static String propertiesFile = "src/resources/application.properties";
    private static String siteURL = "";

    static {
        try {
            siteURL = getProperty("site.url");
        } catch (IOException ignored) {
        }
    }

    public static String getSiteURL() {
        return siteURL;
    }

    public static String getProperty(String property) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
        String regex = String.format("%s=(.+)", property);
        String str = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        while ((str = reader.readLine()) != null) {
            matcher = pattern.matcher(str);
            if (matcher.matches()) return matcher.group(1);
        }
        reader.close();
        return "";
    }

    public static void editProperty(String key, String value) throws IOException {
        if (key.isEmpty() || value.isEmpty()) return;
        if (getProperty(key).isEmpty()) addProperty(key, value);
        Map<String, String> oldProperties = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
        Pattern pattern = Pattern.compile("(.+)\\=(.+)");
        String str = "";
        Matcher matcher;

        while ((str = reader.readLine()) != null) {
            matcher = pattern.matcher(str);
            if (matcher.matches()) {
                oldProperties.put(matcher.group(1), matcher.group(2));
            }
        }
        reader.close();
        oldProperties.replace(key, value);
        BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile));
        for (String k : oldProperties.keySet()) {
            writer.write(String.format("%s=%s\n", k, oldProperties.get(k)));
        }
        writer.close();
    }

    public static void addProperty(String key, String value) throws IOException {
        if (key.isEmpty() || value.isEmpty() || !getProperty(key).isEmpty()) return;
        BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile, true));
        writer.write("\n" + key + "=" + value);
        writer.close();
    }

}
