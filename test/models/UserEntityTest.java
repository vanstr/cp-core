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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

            dropAuth = new Dropbox(originUserEntity.getDropboxAccessKey());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }
    }

    @Test
    public void test1CreateUser(){
        UserEntity user = new UserEntity();
        user.setLogin("testUser");
        user.setPassword("qwerty123");
        user.save();
        UserEntity testUser = UserEntity.getUserByField("login", "testUser");
        assertNotNull(testUser);
        Logger.info("Create user test done");
    }

    @Test
    public void test2UpdateUser(){
        UserEntity user = UserEntity.getUserByField("login", "testUser");
        user.addSongEntity(SongEntity.find.all().get(0));
        user.update();
        UserEntity testUser = UserEntity.getUserByField("login", "testUser");
        assertNotNull(testUser.getSongEntities());
        assertFalse(testUser.getSongEntities().isEmpty());
        Logger.info("Update user test done");
    }

    @Test
    public void test3DeleteUser(){
        UserEntity user = UserEntity.getUserByField("login", "testUser");
        UserEntity.deleteUserById(user.getId());
        assertNull(UserEntity.getUserByField("login", "testUser"));
        Logger.info("Delete user test done");
    }

    @Test
    public void test1GetUserByFields(){
        Map<String, Object> whereClause = new HashMap<String, Object>();
        whereClause.put("login", originUserEntity.getLogin());
        whereClause.put("password", originUserEntity.getPassword());

        UserEntity testUserEntity = UserEntity.getUserByFields(whereClause);

        assertNotNull(testUserEntity);
        assertTrue(testUserEntity.equals(originUserEntity));
        Logger.info("test1GetUserByFields done");
    }


    @Test
    public void test1SaveUser(){
        //TODO
        Logger.info("Save user test done");
    }

}
