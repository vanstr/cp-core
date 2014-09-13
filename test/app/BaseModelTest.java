package app;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;

/**
 * Created by imi on 22.08.2014..
 */
public class BaseModelTest {
  //public static FakeApplication app;

  public static TestServer testServer;

  public static int port = 3333;

  public static String testServerHost = "http://localhost:" + port;

  @BeforeClass
  public static void startApp() {
    FakeApplication app = Helpers.fakeApplication(Helpers.inMemoryDatabase());


    testServer = Helpers.testServer(port, app);

    Helpers.start(testServer);
  }

  @AfterClass
  public static void stopApp() {
    Helpers.stop(testServer);
  }
}