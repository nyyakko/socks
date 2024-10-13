package dev.enche.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {

    public static Map<String, String> parseRequestLine(BufferedReader reader) throws IOException {
        final var result = new HashMap<String, String>();
        final var regex = "^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT) (\\S+) (\\S+)";
        final var matcher = Pattern.compile(regex).matcher(reader.readLine());
        if (matcher.find()) {
            result.put("Method", matcher.group(1));
            result.put("Path", matcher.group(2));
            result.put("Version", matcher.group(3));
        }
        return result;
    }

    public static Map<String, String> parseRequestHeaders(BufferedReader reader) throws IOException {
        final var pattern = Pattern.compile("^(.*?):\\S*(.*)$");
        final var headers = new HashMap<String, String>();
        for (var line = reader.readLine(); line != null && !line.isEmpty(); line = reader.readLine()) {
            final var matcher = pattern.matcher(line);
            if (matcher.find()) {
                headers.put(matcher.group(1), matcher.group(2).strip());
            }
        }
        return headers;
    }

}
