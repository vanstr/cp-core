import javax.ejb.Stateless;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AuthorizationBean implements AuthorizationBeanRemote {

    public String login(String userName, String password) {
        return "Hello !";
    }

    public Boolean registerUser(String userName, String password) {
        return null;
    }

    public String getDropboxAuthLink(Long userId) {
        return null;
    }

    public Boolean retrieveDropboxAccessToken(Long userId) {
        return null;
    }
}
