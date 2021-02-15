package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    String result = "";
    InputStream inputStream;

    public String getConfigValues(String key) throws IOException {

        try {
            Properties config = new Properties();
            String file = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(file);

            if (inputStream != null) {
                config.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file not found in the classpath.");
            }

            result = config.getProperty(key);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }

        return result;
    }
}
