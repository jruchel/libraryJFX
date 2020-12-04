package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Resources {

    private static String path = "src/resources";

    public static String getStyle(String name) throws IOException {
        return getResource("style", name, "css");
    }

    private static String getResource(String type, String file, String extension) throws IOException {
        String currentPath = path;
        if (!type.isEmpty()) {
            currentPath += "/" + type + "s";
        }
        currentPath += "/" + file;
        currentPath += "." + extension;
        return readFile(currentPath);
    }

    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder sb = new StringBuilder();
        String str = "";
        while ((str = reader.readLine()) != null) {
            sb.append(str);
        }
        return sb.toString();
    }

}
