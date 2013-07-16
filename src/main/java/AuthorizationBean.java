import cloud.Dropbox;
import commons.Tokens;
import persistence.UserEntity;
import persistence.UserManager;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */

public class AuthorizationBean implements AuthorizationBeanRemote {

    @Override
    public Long login(String userName, String password) {
        return 1L;
    }

    @Override
    public Boolean registerUser(String userName, String password) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDropboxAuthLink(Long userId) {
        String link = null;
        Boolean res = false;

        Dropbox drop = new Dropbox();

        Tokens requestTokens = drop.getRequestTokens();

        // save requestTokens to DB
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(userId);
        user.setDropboxRequestKey(requestTokens.key);
        user.setDropboxRequestSecret(requestTokens.secret);
        res = manager.updateUser(user);
        manager.finalize();
        if ( res == false ){
            //  error
        }

        link = drop.getAuthLink();

        return link;
    }

    @Override
    public Boolean retrieveDropboxAccessToken(Long userId) {
        boolean res = false;

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

        return res;
    }
}