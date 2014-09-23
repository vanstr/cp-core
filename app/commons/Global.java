package commons;

import play.Application;
import play.GlobalSettings;

/**
 * Created by alex on 9/24/14.
 */
public class Global extends GlobalSettings {
    @Override
    public void onStart(Application application) {
        SystemProperty initializator = new SystemProperty();
        initializator.fetchProperties();
    }
}
