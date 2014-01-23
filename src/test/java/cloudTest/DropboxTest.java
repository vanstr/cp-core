package cloudTest;

import cloud.Dropbox;
import commons.Tokens;
import org.junit.BeforeClass;
import org.junit.Test;
import persistence.UserEntity;
import persistence.UserManager;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.17.7
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class DropboxTest {

    private static Dropbox dropUnAuth = null; // un authorized dropboz session
    private static Dropbox dropAuth = null; // authorized dropboz session

    private static String incorrectMusicFile = "/balaslkdjasc.mp3";
    private static String correctMusicFile = "/JUnit/music.mp3";

    @BeforeClass
    public static void method() {
        try {
            dropUnAuth = new Dropbox();


            // ATTENTION user with 1 id should be JUnit user and with access keys
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(1);

            dropAuth = new Dropbox(user.getDropboxAccessKey(), user.getDropboxAccessSecret());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("error in preparing");
        }

    }

    @Test
    public void testDropbox(){

        try{
            new Dropbox(null, null);
        }catch( Exception e){
            return;
        }

        fail("testDropbox frong result with NULL key pair value");
    }

    @Test
    public void testGetRequestTokens(){

        Tokens res = dropUnAuth.getRequestTokens();
        if (res.secret.isEmpty() || res.key.isEmpty()) fail("key or secret is empty");
    }

    @Test
    public void testGetAuthLink(){

        String res = dropUnAuth.getAuthLink();
        if (!res.contains("https://")) fail("URl doesnt contain https://");
    }

    // We assume that user has requestTokens in DB
    @Test
    public void testGetUserAccessTokens(){

        // 1. User not provided access to his account, -> get exception in Dropbox class
        Tokens res = dropUnAuth.getRequestTokens();
        Tokens accessTokens = null;
        try {
            accessTokens = dropUnAuth.getUserAccessTokens(res);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        // accessTokens must be null because not provided access via link to account
        assertNull("AccessTokens should be NULL!", accessTokens);

        // 2.User has provided access to his account
        // assume that user has requestTokens in DB
        // TODO: get user requestTokens, provide access to acc via link, retrieve accessTokens, save them to DB and update dropAuth
        // Problem: user should refer to URL and provide access
        /*
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(1);

        Tokens res1 = new Tokens(user.getDropboxRequestKey(), user.getDropboxRequestSecret());
        Tokens accessTokens1 = dropAuth.getUserAccessTokens(res1);

        if( accessTokens1 == null ) fail("AccessTokens1 should not be NULL!");
        if( accessTokens1.secret.isEmpty() || accessTokens1.key.isEmpty() ) fail("key or secret is empty");

        user.setDropboxAccessKey(accessTokens1.key);
        user.setDropboxAccessSecret(accessTokens1.secret);
        manager.updateUser(user);
        manager.finalize();
        */
    }

    @Test
    public void testGetFileLink(){


        // 1. file exists
        String res = null; // http://dl.dropboxusercontent.com/1/view/n8cbbuw08p669ku/JUnit/music.mp3
        try {
            res = dropAuth.getFileLink(correctMusicFile);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Fail not found");
        }
        if (!res.endsWith(correctMusicFile)) fail("Bad file link:" + res);

        // 2. file doesnt exist
        String res2 = null;
        try {
            res2 = dropAuth.getFileLink(incorrectMusicFile);
        } catch (Exception e) {
        }
        assertNull("Bad file link", res2);

    }

    @Test
    public void testGetFileList(){

        ArrayList<String> fileTypes = new ArrayList<String>();
        fileTypes.add("mp3");

        // 1. should be exception in method because dropbox session not established
        // unworked
        List<String[]> res5 = null;
        try {
            res5 = dropUnAuth.getFileList("/", true, fileTypes);
        } catch (Exception e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assertNull("Exception", res5);

        // 2.
        List<String[]> res = null;
        try {
            res = dropAuth.getFileList("/", true, fileTypes);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            fail("Exception");
        }

        // check does searched file presents in music list
        boolean filePresents = false;

        int resSize = res.size();
        for (int i = 0; i < resSize; i++) {
            System.out.println(res.get(i));
            if (correctMusicFile.equals(res.get(i)[1])) filePresents = true;
        }
        assertTrue("Music file not found", filePresents);

    }

}
