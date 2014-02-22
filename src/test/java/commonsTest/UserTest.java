package commonsTest;

import cloud.Dropbox;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.UserEntity;
import persistence.manage.UserManager;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.22.2
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

    final static Logger logger = LoggerFactory.getLogger(TagTest.class);


    private static Dropbox dropUnAuth = null; // un authorized dropboz session
    private static Dropbox dropAuth = null; // authorized dropboz session
    private static UserManager userManager = null;
    private static UserEntity user  = null;


    @BeforeClass
    public static void method() {
        try {
            dropUnAuth = new Dropbox();

            // ATTENTION user with 1 id should be JUnit user and with access keys
            userManager = new UserManager();

            user = userManager.getUserById(1);

            System.out.println(userManager.getSessionStatistic());

            dropAuth = new Dropbox(user.getDropboxAccessKey(), user.getDropboxAccessSecret());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }

    }

    @Test
    public void test1SaveSongs(){

    }

    @AfterClass
    public static void end() {
        userManager.finalize();
    }

}
