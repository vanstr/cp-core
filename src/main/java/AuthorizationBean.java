import javax.ejb.Stateless;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
@Stateless(name = "AuthorizationBean", mappedName = "ejb/AuthorizationBean")
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
    public String getDropbocAuthLink(Long userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean retrieveDropboxAccessToken(Long userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
