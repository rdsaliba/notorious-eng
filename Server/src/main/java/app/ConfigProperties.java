package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigProperties {

    static Logger logger = LoggerFactory.getLogger(ConfigProperties.class);
    String result = "";

    InputStream inputStream;

    public String getConfigValues(String key) throws IOException {

        try {
            Properties config = new Properties();
            String file = "config.properties";

            inputStream = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file));
            config.load(inputStream);

            result = config.getProperty(key);

        } catch (Exception e) {
            logger.error("Exception: { }", e);
        } finally {
            inputStream.close();
        }

        return result;
    }
}
