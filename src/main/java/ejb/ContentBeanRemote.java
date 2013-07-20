package ejb;

import javax.ejb.Remote;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: user
 * Date: 7/5/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */

@Remote
public interface ContentBeanRemote {

    public List<String> getFiles(String folderPath, Boolean recursive, Long userId);

    public String getFileSrc(String path, Long userId);
}
