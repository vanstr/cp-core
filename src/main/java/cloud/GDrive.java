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
    private static String DRIVE_EMAIL_URL = Initializator.getLocalProperties().getProperty("drive.email.url");
    private static String DRIVE_TOKEN_URL = Initializator.getLocalProperties().getProperty("drive.token.url");
    private static String DRIVE_FILES_URL = Initializator.getLocalProperties().getProperty("drive.files.url");
    private static String DRIVE_SCOPE_URL = Initializator.getLocalProperties().getProperty("drive.scope.url");
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
        params.put("scope", DRIVE_EMAIL_URL + "+" + DRIVE_SCOPE_URL);
        JSONObject object = HttpWorker.sendPostRequest(DRIVE_TOKEN_URL, params);
        OAuth2UserData oAuth2UserData = parseDriveData(object);
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
        String url = DRIVE_FILES_URL + "?oauth_token=" + this.accessToken;
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
            JSONObject object = HttpWorker.sendPostRequest(DRIVE_TOKEN_URL, params);
            accessToken = object.getString("access_token");
            this.tokenExpires = object.getLong("expires_in")*1000 + System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getFileLink(String fileId){
        JSONObject object = HttpWorker.sendGetRequest(DRIVE_FILES_URL
                + fileId + "?oauth_token=" + this.accessToken);
        String fileSrc = object.getString("downloadUrl") + "&oauth_token=" + this.accessToken;
        return fileSrc;
    }

    public static OAuth2UserData parseDriveData(JSONObject jsonObject){
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.setAccessToken(jsonObject.getString("access_token"));
        oAuth2UserData.setRefreshToken(jsonObject.getString("refresh_token"));
        oAuth2UserData.setExpiresIn(jsonObject.getInt("expires_in"));
        JSONObject object = HttpWorker.sendGetRequest(DRIVE_EMAIL_URL + "?alt=json&oauth_token="
                + oAuth2UserData.getAccessToken());
        String email = object.getJSONObject("data").get("email").toString();
        oAuth2UserData.setUniqueCloudId(email);
        return oAuth2UserData;
    }
}
