package commons;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
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
        if(localProperties == null){
            Initializator initializator = new Initializator();
            initializator.atStartup();
            return initializator.getProperties();
        }
        return localProperties;
    }

    public Properties getProperties(){
        return localProperties;
    }
}