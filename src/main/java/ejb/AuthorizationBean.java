package ejb;

import cloud.Dropbox;
import cloud.GDrive;
import commons.Tokens;
import persistence.UserEntity;
import persistence.utility.UserManager;

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
        Long result = null;
        UserManager manager = new UserManager();
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("login", login);
        fieldMap.put("password", password);
        List<UserEntity> list = manager.getUsersByFields(fieldMap);
        if (list != null && list.size() > 0 ) {
            result = list.get(0).getId();
        }
        manager.finalize();
        return result;
    }

    @Override
    public Boolean registerUser(String login, String password) {
        boolean result = false;
        UserManager manager = new UserManager();
        try {
            UserEntity newUser = new UserEntity();
            newUser.setLogin(login);
            newUser.setPassword(password);
            result = manager.addUser(newUser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result;
        }
    }

    /**
     * @param userId
     * @return String - link, where user should provide access to his account for this application
     *         null - error
     */
    @Override
    public String getDropboxAuthLink(Long userId) {
        String link = null;
        UserManager manager = new UserManager();

        try {
            Dropbox drop = new Dropbox();

            Tokens requestTokens = drop.getRequestTokens();

            // save requestTokens to DB
            UserEntity user = manager.getUserById(userId);
            user.setDropboxRequestKey(requestTokens.key);
            user.setDropboxRequestSecret(requestTokens.secret);
            boolean res = manager.updateUser(user);

            // tokens was not saved
            if (res == false) throw new Exception(EXCEPTION_DB_EXECUTION_ERROR);

            link = drop.getAuthLink();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return link;
        }

    }

    /**
     * @param userId
     * @return true - user has provided access to app and access tokens saved.
     *         false - error occurred
     */
    @Override
    public Boolean retrieveDropboxAccessToken(Long userId) {
        boolean result = false;
        UserManager manager = new UserManager();

        try {
            // Work with dropbox service, start session
            Dropbox drop = new Dropbox();

            // get requestTokens from db
            UserEntity user = manager.getUserById(userId);
            Tokens requestTokens = new Tokens(user.getDropboxRequestKey(), user.getDropboxRequestSecret());

            // retrive AccessToken
            Tokens accessTokens = drop.getUserAccessTokens(requestTokens);

            // save accessTokens to DB
            user.setDropboxAccessKey(accessTokens.key);
            user.setDropboxAccessSecret(accessTokens.secret);
            result = manager.updateUser(user);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result;
        }

    }

    @Override
    public Boolean retrieveGDriveCredentials(Long userId, String code) {
        Boolean result = false;
        UserManager manager = new UserManager();

        try {
            UserEntity user = manager.getUserById(userId);
            if (user != null) {
                GDrive gDrive = new GDrive(null, null);
                // retrive AccessToken
                Map<String, String> credentials = gDrive.retrieveAccessToken(code);
                String accessToken = credentials.get("access_token");
                String refreshToken = credentials.get("refresh_token");

                // save accessTokens to DB
                user.setDriveAccessToken(accessToken);
                user.setDriveRefreshToken(refreshToken);
                result = manager.updateUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result;
        }
    }

    @Override
    public Boolean removeDropboxAcoount(Long userId) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try {
            UserEntity user = manager.getUserById(userId);
            if (user != null) {
                user.setDropboxAccessKey(null);
                user.setDropboxAccessSecret(null);
                result = manager.updateUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result;
        }
    }

    @Override
    public Boolean removeGDriveAccount(Long userId) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try {
            UserEntity user = manager.getUserById(userId);
            if (user != null) {
                user.setDriveAccessToken(null);
                user.setDriveRefreshToken(null);
                result = manager.updateUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result;
        }
    }
}