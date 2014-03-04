package ejb;

import commons.CloudFile;

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

    public static final Integer DROPBOX_CLOUD_ID = 1;
    public static final Integer DRIVE_CLOUD_ID = 2;

    public List<CloudFile> getFiles(String folderPath, Long userId);

    public String getFileSrc(Integer cloudId, String path, Long userId, String fileId);
}
