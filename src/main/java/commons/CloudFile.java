package commons;


import com.dropbox.core.DbxUrlWithExpiration;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.26.12
 * Time: 14:39
 * To change this template use CloudFile | Settings | CloudFile Templates.
 */
public class CloudFile implements Serializable {

    private Integer cloudId;
    private String name;
    private String url;
    private Long expires;

    public CloudFile(Integer cloudId, String path, String url, Long expires){
        this.cloudId = cloudId;
        this.name = path;
        this.url = url;
        this.expires = expires - System.currentTimeMillis();
    }

    public Integer getCloudId() {
        return cloudId;
    }

    public void setCloudId(Integer cloudId) {
        this.cloudId = cloudId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = filename.lastIndexOf('.');
        return filename.substring(index + 1).toLowerCase();
    }



    public static boolean checkFileType(String fileName, List<String> requestedFileTypes){

        if(fileName == null){
            return false;
        }
        fileName = fileName.toLowerCase();
        boolean result = false;

        if(requestedFileTypes == null ){
            result = true;
        }else{
            String fileType = getExtension(fileName);
            if( requestedFileTypes.contains(fileType) ){

                int nameLength = fileName.length();
                int extensionLength = fileType.length();

                // file name ".mp3" - not allowed, at least "a.mp3"
                if (nameLength > (extensionLength + 2)){
                    result = true;
                }

            }

        }

        return result;

    }
}
