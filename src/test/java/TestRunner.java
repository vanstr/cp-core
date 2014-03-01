import cloudTest.DropboxTest;
import commons.SongMetadataPopulationTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import persistenceTest.SongTest;
import persistenceTest.UserTest;

public class TestRunner {
    public static void main(String[] args) {




        Result result1 = JUnitCore.runClasses(SongMetadataPopulationTest.class);
        for (Failure failure : result1.getFailures()) {
            System.out.println(failure.toString());
        }

        Result result = JUnitCore.runClasses(UserTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        //* Dropbox
        Result resultDropbox = JUnitCore.runClasses(DropboxTest.class);
        for (Failure failure : resultDropbox.getFailures()) {
            System.out.println(failure.toString());
        }
        //*/

        //*
        Result resultPersistence = JUnitCore.runClasses(SongTest.class);
        for (Failure failure : resultPersistence.getFailures()) {
            System.out.println(failure.toString());
        }
        // */

        //System.out.println(EntityManager.getSessionStatistic());
    }
} 