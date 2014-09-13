package persistenceTest;

import clouds.Dropbox;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.UserEntity;
import persistence.utility.UserManager;

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

    final static Logger logger = LoggerFactory.getLogger(UserTest.class);


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

            logger.debug(userManager.getSessionStatistic());

            dropAuth = new Dropbox(user.getDropboxAccessKey());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }
        logger.info("BeforeClass done");

    }

    @Test
    public void test1SaveUser(){
        //TODO
    }

    @AfterClass
    public static void end() {
        userManager.finalize();
        logger.info("AfterClass done");
    }

}
