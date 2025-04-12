package hr.tvz.cartographers.shared.jndi;

import hr.tvz.cartographers.shared.enums.NetworkConfiguration;
import hr.tvz.cartographers.shared.exception.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationReader {
    private static final String CONFIGURATION_FILE_NAME = "application.properties";
    private static final Map<String, String> environment = new HashMap<>();

    static {
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        String resourcesPath = new File("src/main/resources").getAbsolutePath();
        environment.put(Context.PROVIDER_URL, "file:///" + resourcesPath.replace("\\", "/"));
    }

    public static String getStringValue(NetworkConfiguration key) {
        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)) {
            Object lookupResult = context.lookup(CONFIGURATION_FILE_NAME);
            File file = (File) lookupResult;
            Properties props = new Properties();

            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            }

            String value = props.getProperty(key.getValue());
            if (value == null)
                throw new CustomException("Configuration key " + key.getValue() + " not found in " + CONFIGURATION_FILE_NAME);

            return value;
        } catch (NamingException | IOException e) {
            throw new CustomException("Failed to read the configuration key " + key.getValue(), e);
        }
    }

    public static Integer getIntegerValue(NetworkConfiguration key) {
        try {
            return Integer.valueOf(getStringValue(key));
        } catch (NumberFormatException e) {
            throw new CustomException("Configuration key " + key.getValue() + " is not a valid integer", e);
        }
    }
}
