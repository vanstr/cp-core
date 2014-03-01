package ejb;

import cloud.Dropbox;
import cloud.GDrive;
import commons.HttpWorker;
import commons.Tokens;
import org.codehaus.jackson.map.util.JSONPObject;
import org.json.JSONObject;
import persistence.UserEntity;
import persistence.UserManager;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class AuthorizationBean implements AuthorizationBeanRemote {

    private static final String EXCEPTION_DB_EXECUTION_ERROR = "EXCEPTION_DB_EXECUTION_ERROR";

    @Override
    public Long login(String login, String password) {
        UserManager userManager = new UserManager();
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("login", login);
        fieldMap.put("password", password);
        List<UserEntity> list = userManager.getUsersByFields(fieldMap);
        if(list == null || list.size() < 1){
            return null;
        }
        userManager.finalize();
        return list.get(0).getId();
    }

    @Override
    public Boolean registerUser(String login, String password) {
        UserManager userManager = null;
        try{
            userManager = new UserManager();
            UserEntity newUser = new UserEntity();
            newUser.setLogin(login);
            newUser.setPassword(password);
            userManager.addUser(newUser);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(userManager != null){
                userManager.finalize();
            }
        }
        return true;
    }

    /**
     *
     * @param userId
     * @return
     *      String - link, where user should provide access to his account for this application
     *      null - error
     */
    @Override
    public String getDropboxAuthLink(Long userId) {
        String link = null;

        try {
            Dropbox drop = new Dropbox();

            Tokens requestTokens = drop.getRequestTokens();

            // save requestTokens to DB
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);
            user.setDropboxRequestKey(requestTokens.key);
            user.setDropboxRequestSecret(requestTokens.secret);
            boolean res = manager.updateUser(user);
            manager.finalize();

            // tokens was not saved
            if (res == false) throw new Exception(EXCEPTION_DB_EXECUTION_ERROR);

            link = drop.getAuthLink();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return link;
        }

    }

    /**
     *
     * @param userId
     * @return
     *      true - user has provided access to app and access tokens saved.
     *      false - error occurred
     */
    @Override
    public Boolean retrieveDropboxAccessToken(Long userId) {
        boolean res = false;

        try {
            // Work with dropbox service, start session
            Dropbox drop = new Dropbox();

            // get requestTokens from db
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);
            Tokens requestTokens = new Tokens(user.getDropboxRequestKey(), user.getDropboxRequestSecret());

            // retrive AccessToken
            Tokens accessTokens = drop.getUserAccessTokens(requestTokens);

            // save accessTokens to DB
            user.setDropboxAccessKey(accessTokens.key);
            user.setDropboxAccessSecret(accessTokens.secret);
            res = manager.updateUser(user);
            manager.finalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }

    }

    @Override
    public Boolean retrieveGDriveCredentials(Long userId, String code) {
        Boolean result = false;

        try {
            GDrive gDrive = new GDrive(null, null);

            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);

            if(user == null){
                return false;
            }
            // retrive AccessToken
            Map<String, String> credentials = gDrive.retrieveAccessToken(code);
            String accessToken = credentials.get("access_token");
            String refreshToken = credentials.get("refresh_token");

            // save accessTokens to DB
            user.setDriveAccessToken(accessToken);
            user.setDriveRefreshToken(refreshToken);
            result = manager.updateUser(user);
            manager.finalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Boolean removeDropboxAcoount(Long userId) {
        Boolean result = false;
        try{
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);
            if(user == null){
                return false;
            }

            user.setDropboxAccessKey(null);
            user.setDropboxAccessSecret(null);
            result = manager.updateUser(user);
            manager.finalize();
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Boolean removeGDriveAccount(Long userId) {
        Boolean result = false;
        try{
            UserManager manager = new UserManager();
            UserEntity user = manager.getUserById(userId);
            if(user == null){
                return false;
            }

            user.setDriveAccessToken(null);
            user.setDriveRefreshToken(null);
            result = manager.updateUser(user);
            manager.finalize();
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Long authorizeWithDrive(String code) {
        Long userId = null;
        GDrive gDrive = new GDrive(null, null);
        Map<String, String> tokens = gDrive.retrieveAccessToken(code);
        UserManager userManager = new UserManager();
        JSONObject object = HttpWorker.sendGetRequest("https://www.googleapis.com/userinfo/email?alt=json&oauth_token="
                + tokens.get("access_token"));
        String email = object.getJSONObject("data").get("email").toString();
        List<UserEntity> userList = userManager.getUsersByField("google_email", email);
        if(userList == null || userList.size() == 0){
            UserEntity user = new UserEntity();
            user.setDriveAccessToken(tokens.get("access_token"));
            user.setDriveRefreshToken(tokens.get("refresh_token"));
            user.setGoogleEmail(email);
            userId = userManager.addUser(user);
        }else{
            UserEntity user = userList.get(0);
            user.setDriveAccessToken(tokens.get("access_token"));
            user.setDriveRefreshToken(tokens.get("refresh_token"));
            userId = user.getId();
            userManager.updateUser(user);
        }
        userManager.finalize();
        return userId;
    }
}