import commonsTest.TagTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        /* Dropbox
        Result result = JUnitCore.runClasses(DropboxTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        //*/

        //*
        Result resultPersistence = JUnitCore.runClasses(TagTest.class);
        for (Failure failure : resultPersistence.getFailures()) {
            System.out.println(failure.toString());
        }
        // */
    }
} 