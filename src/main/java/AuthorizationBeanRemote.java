import javax.ejb.Remote;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Remote
public interface AuthorizationBeanRemote {
    public String login(String userName, String password);

    public Boolean registerUser(String userName, String password);

    //
    public String getDropboxAuthLink(Long userId);

    //save user tokens to DB
    public Boolean retrieveDropboxAccessToken(Long userId);
}
