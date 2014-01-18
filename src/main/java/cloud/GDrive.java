package cloud;

import com.google.api.services.drive.DriveScopes;
import com.sun.servicetag.UnauthorizedAccessException;
import commons.CloudFile;
import commons.HttpWorker;
import commons.Initializator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GDrive {

    private static String CLIENT_ID = Initializator.getLocalProperties().getProperty("drive.client.id");
    private static String CLIENT_SECRET = Initializator.getLocalProperties().getProperty("drive.client.secret");
    private static String REDIRECT_URI = Initializator.getLocalProperties().getProperty("drive.redirect.uri");
    private static final String GRANT_TYPE = "refresh_token";

    private String accessToken;
    private String refreshToken;

    public GDrive(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Map<String, String> retrieveAccessToken(String code){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", GRANT_TYPE);
        params.put("redirect_uri", REDIRECT_URI);
        params.put("scope", DriveScopes.DRIVE);
        JSONObject object = HttpWorker.sendPostRequest("https://accounts.google.com/o/oauth2/token", params);
        Map<String, String> tokens = new HashMap<String, String>();
        tokens.put("access_token", object.getString("access_token"));
        tokens.put("refresh_token", object.getString("refresh_token"));
        return tokens;
    }

    public Map<String, String> getFileList(String folderPath, boolean recursive, List<String> fileTypes){
        Map<String, String> files = null;
        try {
            files = retrieveAllFiles(this.accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<Map.Entry<String,String>> iter = files.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,String> entry = iter.next();
            if ( !CloudFile.checkFileType(entry.getKey(), fileTypes) ) {
                iter.remove();
            }
        }
        return files;
    }

    public Map<String, String> retrieveAllFiles(String accessToken) throws IOException {
        Map<String, String> result = new HashMap<String, String>();
        String url = "https://www.googleapis.com/drive/v2/files?oauth_token=" + accessToken;
        JSONObject object = HttpWorker.sendGetRequest(url);
        JSONArray fileArray = object.getJSONArray("items");
        for(int i = 0; i < fileArray.length(); i++){
            if(!fileArray.getJSONObject(i).getJSONObject("labels").getBoolean("trashed")
                    && !"application/vnd.google-apps.folder".equals(fileArray.getJSONObject(i).getString("mimeType"))
                    && fileArray.getJSONObject(i).has("title")
                    && fileArray.getJSONObject(i).has("downloadUrl")){
                result.put(fileArray.getJSONObject(i).getString("title"),
                        fileArray.getJSONObject(i).getString("downloadUrl") + "&oauth_token=" + this.accessToken);
            }
        }
        return result;
    }

    public String refreshToken(String refreshToken){
        String accessToken = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("client_id", CLIENT_ID);
            params.put("client_secret", CLIENT_SECRET);
            params.put("grant_type", GRANT_TYPE);
            params.put("refresh_token", refreshToken);
            JSONObject object = HttpWorker.sendPostRequest("https://accounts.google.com/o/oauth2/token", params);
            accessToken = object.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getFileLink(String fileId){
        JSONObject object = HttpWorker.sendGetRequest("https://www.googleapis.com/drive/v2/files/"
                + fileId + "?oauth_token=" + this.accessToken);
        String fileSrc = object.getString("downloadUrl") + "&oauth_token=" + this.accessToken;
        return fileSrc;
    }
}
