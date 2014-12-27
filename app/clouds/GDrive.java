package clouds;

import structures.OAuth2UserData;
import commons.CloudFile;
import commons.HttpWorker;
import commons.SystemProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import structures.Song;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


public class GDrive implements Cloud {

    private static final String GRANT_TYPE_REFRESH = "refresh_token";
    private static final String GRANT_TYPE_AUTHORIZATION = "authorization_code";
    public static final String DRIVE_OAUTH_URL = "https://www.googleapis.com/drive/v2/files?oauth_token=";

    private String accessToken;
    private String refreshToken;
    private Long tokenExpires;

    public GDrive(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public GDrive() {
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

    @Override
    public List<Song> getFileList(String folderPath, List<String> requestedFileTypes) {
        List<Song> files = new ArrayList<Song>();
        try {
            files = retrieveAllFiles();
        } catch (IOException e) {
            Logger.error("Exception in getFileList " + e.getMessage());
        }

        Iterator<Song> i = files.iterator();
        while (i.hasNext()) {
            Song track = i.next();

            if (!CloudFile.checkFileType(track.getFileName(), requestedFileTypes)) {
                i.remove();
            }
        }
        return files;
    }


    @Override
    public String getFileLink(String fileId) {
        JSONObject object = HttpWorker.sendGetRequest(SystemProperty.DRIVE_FILES_URL
                + fileId + "?oauth_token=" + this.accessToken);
        String fileSrc = null;
        try {
            fileSrc = object.getString("downloadUrl") + "&oauth_token=" + this.accessToken;
        } catch (JSONException e) {
            Logger.error("Exception in getFileLink", e);
        }
        return fileSrc;
    }

    @Override
    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl) {
        JSONObject object = retrieveAccessToken(code, SystemProperty.DRIVE_CLIENT_ID,
                SystemProperty.DRIVE_CLIENT_SECRET, GRANT_TYPE_AUTHORIZATION, redirectUrl,
                SystemProperty.DRIVE_EMAIL_SCOPE + "+" + SystemProperty.DRIVE_SCOPE, SystemProperty.DRIVE_TOKEN_URL);
        OAuth2UserData oAuth2UserData = null;
        try {
            oAuth2UserData = parseDriveData(object);
        } catch (JSONException e) {
            Logger.error("Exception in retrieveAccessToken", e);
        }
        return oAuth2UserData;
    }

    @Override
    public String refreshToken(String refreshToken) {
        String accessToken = null;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("client_id", SystemProperty.DRIVE_CLIENT_ID);
            params.put("client_secret", SystemProperty.DRIVE_CLIENT_SECRET);
            params.put("grant_type", GRANT_TYPE_REFRESH);
            params.put("refresh_token", refreshToken);
            JSONObject object = HttpWorker.sendPostRequest(SystemProperty.DRIVE_TOKEN_URL, params);
            accessToken = object.getString("access_token");
            this.tokenExpires = object.getLong("expires_in") * 1000 + System.currentTimeMillis();
        } catch (Exception e) {
            Logger.error("Exception in refreshToken", e);
        }
        return accessToken;
    }

    private static OAuth2UserData parseDriveData(JSONObject jsonObject) throws JSONException {
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.setAccessToken(jsonObject.getString("access_token"));
        oAuth2UserData.setRefreshToken(jsonObject.getString("refresh_token"));
        oAuth2UserData.setExpiresIn(jsonObject.getInt("expires_in"));
        JSONObject object = HttpWorker.sendGetRequest(SystemProperty.DRIVE_EMAIL_URL + "?alt=json&oauth_token="
                + oAuth2UserData.getAccessToken());
        String email = object.getJSONObject("data").get("email").toString();
        oAuth2UserData.setUniqueCloudId(email);
        return oAuth2UserData;
    }

    public JSONObject retrieveAccessToken(String code, String clientId,
                                          String clientSecret, String grantType,
                                          String redirectUri, String scope, String url){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("grant_type", grantType);
        params.put("redirect_uri", redirectUri);
        if(scope != null && !scope.isEmpty()){
            params.put("scope", scope);
        }
        JSONObject object = HttpWorker.sendPostRequest(url, params);
        return object;
    }


    private List<Song> retrieveAllFiles() throws IOException, JSONException {

        String pageToken = "";
        List<Song> files = new ArrayList<Song>();
        do {
            JSONObject object = getJsonObjectResponse(pageToken);
            List<Song> chunkOfFiles = getSongsFromJsonObject(object);

            files.addAll(chunkOfFiles);

            if (object.has("nextPageToken")) {
                pageToken = object.getString("nextPageToken");
            } else {
                pageToken = "";
            }
        } while (hasNextPage(pageToken));

        return files;
    }


    private JSONObject getJsonObjectResponse(String pageToken) throws UnsupportedEncodingException {
        String url = DRIVE_OAUTH_URL + accessToken;
        if (hasNextPage(pageToken)) {
            url += "&pageToken=" + URLEncoder.encode(pageToken, "UTF-8");
        }
        return HttpWorker.sendGetRequest(url);
    }

    private boolean hasNextPage(String pageToken) {
        return pageToken != null && !"".equals(pageToken);
    }

    private List<Song> getSongsFromJsonObject(JSONObject object) {

        List<Song> files = new ArrayList<Song>();
        if (object == null) {
            return null;
        }
        JSONArray fileArray = object.getJSONArray("items");
        Logger.debug("retrieved files size: " + fileArray.length());
        for (int i = 0; i < fileArray.length(); i++) {
            Logger.debug("file:" + fileArray.getJSONObject(i).getString("title"));
            JSONObject obj = fileArray.getJSONObject(i);
            if (isCorrectJson(obj)) {
                Song song = new Song(
                        SystemProperty.DRIVE_CLOUD_ID,
                        obj.getString("id"),
                        obj.getString("title"),
                        obj.getString("downloadUrl") + "&oauth_token=" + this.accessToken,
                        this.tokenExpires
                );
                files.add(song);
            }
        }
        return files;
    }

    private boolean isCorrectJson(JSONObject obj) {
        return !obj.getJSONObject("labels").getBoolean("trashed")
                && !"application/vnd.google-apps.folder".equals(obj.getString("mimeType"))
                && obj.has("title")
                && obj.has("downloadUrl");
    }

}
