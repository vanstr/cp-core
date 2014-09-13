package ejb;

import clouds.Dropbox;
import clouds.GDrive;
import clouds.OAuth2UserData;
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
        Long result = null;
        UserManager manager = new UserManager();
        try {
            UserEntity newUser = new UserEntity();
            newUser.setLogin(login);
            newUser.setPassword(password);
            result  = manager.addUser(newUser);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
            return result > 0;
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
    public Boolean retrieveDropboxCredentials(Long userId, String code) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try {
            // Work with dropbox service, start session
            Dropbox drop = new Dropbox();

            OAuth2UserData oAuth2UserData = drop.retrieveAccessToken(code);
            // get requestTokens from db
            UserEntity user = manager.getUserById(userId);

            // save accessTokens to DB
            user.setDropboxAccessKey(oAuth2UserData.getAccessToken());
            user.setDropboxUid(oAuth2UserData.getUniqueCloudId());
            result = manager.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return result;
    }

    @Override
    public Boolean retrieveGDriveCredentials(Long userId, String code) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try {
            GDrive gDrive = new GDrive(null, null, null);

            UserEntity user = manager.getUserById(userId);

            if(user == null){
                return false;
            }
            // retrive AccessToken
            OAuth2UserData credentials = gDrive.retrieveAccessToken(code);
            String accessToken = credentials.getAccessToken();
            String refreshToken = credentials.getRefreshToken();

            // save accessTokens to DB
            user.setDriveAccessToken(accessToken);
            user.setDriveRefreshToken(refreshToken);
            user.setDriveTokenExpires(credentials.getExpiresIn()*1000 + System.currentTimeMillis());
            user.setGoogleEmail(credentials.getUniqueCloudId());
            result = manager.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return result;
    }

    @Override
    public Boolean removeDropboxAcoount(Long userId) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try{
            UserEntity user = manager.getUserById(userId);
            if(user == null){
                return false;
            }

            user.setDropboxAccessKey(null);
            user.setDropboxUid(null);
            result = manager.updateUser(user);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return result;
    }

    @Override
    public Boolean removeGDriveAccount(Long userId) {
        Boolean result = false;
        UserManager manager = new UserManager();
        try{
            UserEntity user = manager.getUserById(userId);
            if(user == null){
                return false;
            }

            user.setDriveAccessToken(null);
            user.setDriveRefreshToken(null);
            user.setDriveTokenExpires(null);
            user.setGoogleEmail(null);
            result = manager.updateUser(user);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            manager.finalize();
        }
        return result;
    }

    @Override
    public Long authorizeWithDrive(String code) {
        Long userId;
        GDrive gDrive = new GDrive(null, null, null);
        OAuth2UserData oAuth2UserData = gDrive.retrieveAccessToken(code);
        UserManager userManager = new UserManager();

        List<UserEntity> userList = userManager.getUsersByField("google_email", oAuth2UserData.getUniqueCloudId());
        if(userList == null || userList.isEmpty()){
            UserEntity user = new UserEntity();
            user.setDriveAccessToken(oAuth2UserData.getAccessToken());
            user.setDriveRefreshToken(oAuth2UserData.getRefreshToken());
            user.setGoogleEmail(oAuth2UserData.getUniqueCloudId());
            user.setDriveTokenExpires(oAuth2UserData.getExpiresIn()*1000 + System.currentTimeMillis());
            userId = userManager.addUser(user);
        }else{
            UserEntity user = userList.get(0);
            user.setDriveAccessToken(oAuth2UserData.getAccessToken());
            user.setDriveRefreshToken(oAuth2UserData.getRefreshToken());
            user.setDriveTokenExpires(oAuth2UserData.getExpiresIn()*1000 + System.currentTimeMillis());
            userId = user.getId();
            userManager.updateUser(user);
        }
        userManager.finalize();
        return userId;
    }

    @Override
    public Long authorizeWithDropbox(String code) {
        Long userId = null;
        UserManager userManager = new UserManager();
        try {
            Dropbox dropbox = new Dropbox();
            OAuth2UserData oAuth2UserData = dropbox.retrieveAccessToken(code);
            List<UserEntity> userList = userManager.getUsersByField("dropbox_uid", oAuth2UserData.getUniqueCloudId());
            if(userList == null || userList.isEmpty()){
                UserEntity user = new UserEntity();
                user.setDropboxAccessKey(oAuth2UserData.getAccessToken());
                user.setDropboxUid(oAuth2UserData.getUniqueCloudId());
                userId = userManager.addUser(user);
            }else{
                UserEntity user = userList.get(0);
                user.setDropboxAccessKey(oAuth2UserData.getAccessToken());
                userId = user.getId();
                userManager.updateUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userManager.finalize();
        }
        return userId;
    }

}