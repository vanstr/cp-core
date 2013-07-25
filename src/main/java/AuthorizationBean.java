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

    private static final String EXCEPTION_DB_EXECUTION_ERROR = "EXCEPTION_DB_EXECUTION_ERROR";

    @Override
    public Long login(String userName, String password) {
        return 1L;
    }

    @Override
    public Boolean registerUser(String userName, String password) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
}