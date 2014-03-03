package ejb;

import javax.ejb.Remote;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/5/13
 * Time: 11:01 AM
 * TODO better rename USerComandBeanRemote
 */
@Remote
public interface AuthorizationBeanRemote {

    public Long login(String userName, String password);

    public Boolean registerUser(String userName, String password);

    public String getDropboxAuthLink(Long userId);

    //save user tokens to DB
    public Boolean retrieveDropboxCredentials(Long userId, String code);

    public Boolean retrieveGDriveCredentials(Long userId, String code);

    public Boolean removeDropboxAcoount(Long userId);

    public Boolean removeGDriveAccount(Long userId);

    public Long authorizeWithDrive(String code);

    public Long authorizeWithDropbox(String code);

}