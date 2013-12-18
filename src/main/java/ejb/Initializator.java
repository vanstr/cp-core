package ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Startup
@Singleton
public class Initializator {

    public static String APP_KEY;
    public static String APP_SECRET;

    private static Properties localProperties;

    @PostConstruct
    void atStartup() {
        localProperties = new Properties();

        try {
            localProperties.load(Initializator.class.getClassLoader().getResourceAsStream("local.properties"));
            APP_KEY = localProperties.getProperty("app.key");
            APP_SECRET = localProperties.getProperty("app.secret");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    void atShutdown() {
        System.out.println("Bye bye bitches!");
    }

    public static Properties getLocalProperties() {
        return localProperties;
    }
}