package models;

import app.BaseModelTest;
import clouds.Dropbox;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.22.2
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserEntityTest extends BaseModelTest{

    private static Dropbox dropUnAuth = null; // un authorized dropboz session
    private static Dropbox dropAuth = null; // authorized dropboz session



    @BeforeClass
    public static void method() {
        try {
            dropUnAuth = new Dropbox();

            dropAuth = new Dropbox(originUserEntity.dropboxAccessKey);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }
    }

  @Test
  public void test1GetUserByFields(){
    Map<String, Object> whereClause = new HashMap<String, Object>();
    whereClause.put("login", originUserEntity.login);
    whereClause.put("password", originUserEntity.password);

    UserEntity testUserEntity = UserEntity.getUserByFields(whereClause);

    assertNotNull(testUserEntity);
    assertThat(testUserEntity).isEqualTo(originUserEntity);

    Logger.info("test1GetUserByFields done");
  }


  @Test
  public void test1SaveUser(){
    //TODO
  }

}
