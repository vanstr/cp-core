package cloud;

import commons.CloudFile;
import commons.HttpWorker;
import commons.Initializator;
import ejb.ContentBeanRemote;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class GDrive {

    private static String CLIENT_ID = Initializator.getLocalProperties().getProperty("drive.client.id");
    private static String CLIENT_SECRET = Initializator.getLocalProperties().getProperty("drive.client.secret");
    private static String REDIRECT_URI = Initializator.getLocalProperties().getProperty("drive.redirect.uri");
    private static final String GRANT_TYPE_REFRESH = "refresh_token";
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    private String accessToken;
    private String refreshToken;
    private Long tokenExpires;

    public GDrive(String accessToken, String refreshToken, Long tokenExpires){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpires = tokenExpires;
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

    public Long getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(Long tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public OAuth2UserData retrieveAccessToken(String code){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", GRANT_TYPE_AUTHORIZATION);
        params.put("redirect_uri", REDIRECT_URI);
        params.put("scope", "https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/drive");
        JSONObject object = HttpWorker.sendPostRequest("https://accounts.google.com/o/oauth2/token", params);
        OAuth2UserData oAuth2UserData = OAuth2UserData.parseDriveData(object);
        return oAuth2UserData;
    }

    public List<CloudFile> getFileList(String folderPath, List<String> fileTypes){
        List<CloudFile> files = null;
        try {
            files = retrieveAllFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<CloudFile> i = files.iterator();
        while (i.hasNext()) {
            CloudFile track = i.next();
            if ( !CloudFile.checkFileType(track.getName(), fileTypes) ) {
                i.remove();
            }
        }
        return files;
    }

    public List<CloudFile> retrieveAllFiles() throws IOException {
        List<CloudFile> result = new ArrayList<CloudFile>();
        String url = "https://www.googleapis.com/drive/v2/files?oauth_token=" + this.accessToken;
        JSONObject object = HttpWorker.sendGetRequest(url);
        if(object == null){
            return result;
        }
        JSONArray fileArray = object.getJSONArray("items");
        for(int i = 0; i < fileArray.length(); i++){
            if(!fileArray.getJSONObject(i).getJSONObject("labels").getBoolean("trashed")
                    && !"application/vnd.google-apps.folder".equals(fileArray.getJSONObject(i).getString("mimeType"))
                    && fileArray.getJSONObject(i).has("title")
                    && fileArray.getJSONObject(i).has("downloadUrl")){

                CloudFile track = new CloudFile(ContentBeanRemote.DRIVE_CLOUD_ID, fileArray.getJSONObject(i).getString("title"),
                        fileArray.getJSONObject(i).getString("downloadUrl") + "&oauth_token=" + this.accessToken, this.tokenExpires);
                result.add(track);
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
            params.put("grant_type", GRANT_TYPE_REFRESH);
            params.put("refresh_token", refreshToken);
            JSONObject object = HttpWorker.sendPostRequest("https://accounts.google.com/o/oauth2/token", params);
            accessToken = object.getString("access_token");
            this.tokenExpires = object.getLong("expires_in")*1000 + System.currentTimeMillis();
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
