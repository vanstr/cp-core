import cloudTest.DropboxTest;
import commonsTest.TagTest;
import commonsTest.UserTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import persistence.manage.EntityManager;

public class TestRunner {
    public static void main(String[] args) {


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
        Result resultPersistence = JUnitCore.runClasses(TagTest.class);
        for (Failure failure : resultPersistence.getFailures()) {
            System.out.println(failure.toString());
        }
        // */

        System.out.println(EntityManager.getSessionStatistic());
    }
} 