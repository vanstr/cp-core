package clouds;

import clouds.oauth.OAuth2Communicator;
import clouds.oauth.OAuth2UserData;
import commons.CloudFile;
import commons.HttpWorker;
import commons.SystemProperty;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import structure.PlayList;
import structure.Song;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GDrive extends OAuth2Communicator {

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

    public GDrive(String accessToken, String refreshToken, Long tokenExpires) {
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

    public PlayList getFileList(String folderPath, List<String> fileTypes) {
        PlayList playList = null;
        try {
            playList = retrieveAllFiles();
        } catch (IOException e) {
            Logger.error("Exception in getFileList " + e.getMessage());
        }

        Iterator<Song> i = playList.getSongs().iterator();
        while (i.hasNext()) {
            Song track = i.next();

            if (!CloudFile.checkFileType(track.getFileName(), fileTypes)) {
                i.remove();
            }
        }
        return playList;
    }


    public PlayList retrieveAllFiles() throws IOException, JSONException {

        PlayList playList = new PlayList();
        String pageToken = "";
        do {
            JSONObject object = getJsonObjectResponse(pageToken);
            PlayList pagePlayList = getSongsFromJsonObject(object);

            playList.addSongs(pagePlayList.getSongs());

            if (object.has("nextPageToken")) {
                pageToken = object.getString("nextPageToken");
            } else {
                pageToken = "";
            }
        } while (hasNextPage(pageToken));

        return playList;
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

    private PlayList getSongsFromJsonObject(JSONObject object) {

        PlayList playList = new PlayList();
        if (object == null) {
            return playList;
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
                playList.add(song);
            }
        }
        return playList;
    }

    private boolean isCorrectJson(JSONObject obj) {
        return !obj.getJSONObject("labels").getBoolean("trashed")
                && !"application/vnd.google-apps.folder".equals(obj.getString("mimeType"))
                && obj.has("title")
                && obj.has("downloadUrl");
    }

    public String getFileLink(String fileId) {
        JSONObject object = HttpWorker.sendGetRequest(SystemProperty.DRIVE_FILES_URL
                + fileId + "?oauth_token=" + this.accessToken);
        String fileSrc = null;
        try {
            fileSrc = object.getString("downloadUrl")
                    + "&oauth_token="
                    + this.accessToken;
        } catch (JSONException e) {
            Logger.error("Exception in getFileLink", e);
        }
        return fileSrc;
    }

    @Override
    public OAuth2UserData retrieveAccessToken(String code, String redirectUrl) {
        JSONObject object = super.retrieveAccessToken(code, SystemProperty.DRIVE_CLIENT_ID,
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
}
