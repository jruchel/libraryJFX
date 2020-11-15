package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Requests {

    private static Requests instance;
    private List<String> cookies;

    public static Requests getInstance() {
        if (instance == null) instance = new Requests();
        return instance;
    }

    private Requests() {
        cookies = new ArrayList<>();
    }

    public String getResponseBody(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setRequestMethod("GET");
        addCookies(connection);
        saveCookies(connection);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return reader.lines().collect(Collectors.joining());
    }

    private void saveCookies(HttpURLConnection connection) {
        List<String> temp = connection.getHeaderFields().get("Set-Cookie");
        if (temp != null) {
            cookies = temp;
        }
    }

    private void addCookies(HttpURLConnection connection) {
        for (String cookie : cookies) {
            connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
        }
    }

    public int sendPostRequest(String url) throws IOException {
        return sendPostRequest(url, new HashMap<>());
    }

    public int sendPostRequest(String url, String data) throws IOException {
        URL url1 = new URL(url);
        URLConnection con = url1.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json; utf-8");
        http.setRequestProperty("Accept", "application/json");
        addCookies(http);
        try (OutputStream os = http.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        http.connect();
        saveCookies(http);
        return http.getResponseCode();
    }

    public int sendDeleteRequest(String url) throws IOException {
        return sendDeleteRequest(url, "");
    }

    public int sendDeleteRequest(String url, String data) throws IOException {
        URL url1 = new URL(url);
        URLConnection con = url1.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("DELETE");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json; utf-8");
        http.setRequestProperty("Accept", "application/json");
        addCookies(http);
        try (OutputStream os = http.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        http.connect();
        saveCookies(http);
        return http.getResponseCode();
    }

    public int sendPostRequest(String url, Map<String, String> body) throws IOException {
        URL url1 = new URL(url);
        URLConnection con = url1.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json; utf-8");
        http.setRequestProperty("Accept", "application/json");
        addCookies(http);
        StringBuilder stringBuilder = new StringBuilder();

        if (body.keySet().size() > 0) {
            stringBuilder.append("{");
            for (String key : body.keySet()) {
                stringBuilder.append("\"").append(key).append("\"").append(":").append("\"").append(body.get(key)).append("\"").append(",");
            }

            String json = stringBuilder.substring(0, stringBuilder.length() - 1);
            json = json + "}";

            try (OutputStream os = http.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

        }
        http.connect();
        saveCookies(http);
        return http.getResponseCode();
    }

}
