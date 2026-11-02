package pl.devlab.util;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {

    private JsonUtil() {}

    public static String getJsonFromFile(String path) throws IOException {
        try (InputStream stream = JsonUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Unable to find Json file in provided path: " + path);
            }
            return new String(stream.readAllBytes());
        }
    }
}
