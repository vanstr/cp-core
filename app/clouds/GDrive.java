package clouds;

import commons.CloudFile;
import commons.HttpWorker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import play.Logger;
import structures.OAuth2UserData;
import structures.Song;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static commons.SystemProperty.*;


public class GDrive implements Cloud {

    private static final String GRANT_TYPE_REFRESH = "refresh_token";
    public static final String DRIVE_OAUTH_URL = "https://www.googleapis.com/drive/v2/files?oauth_token=";

    private String accessToken;
    private Long tokenExpires;

    public GDrive() {
    }

    public GDrive(String driveRefreshToken) {
        refreshAccessToken(driveRefreshToken);
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
    public Boolean uploadFileByUrl(String fullDestPath, URL link) {
        return null;
    }


    @Override
    public String getFileLink(String fileId) {
        String fileRequestUrl = DRIVE_FILES_URL + fileId + "?oauth_token=" + accessToken;
        JSONObject object = HttpWorker.sendGetRequest(fileRequestUrl);
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
        JSONObject object = HttpWorker.retrieveAccessToken(code, DRIVE_CLIENT_ID,
                DRIVE_CLIENT_SECRET, GRANT_TYPE_AUTHORIZATION, redirectUrl,
                DRIVE_EMAIL_SCOPE + "+" + DRIVE_SCOPE, DRIVE_TOKEN_URL);
        OAuth2UserData oAuth2UserData = null;
        try {
            oAuth2UserData = parseDriveData(object);
        } catch (JSONException e) {
            Logger.error("Exception in retrieveAccessToken", e);
        }
        return oAuth2UserData;
    }

    private void refreshAccessToken(String driveRefreshToken) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("client_id", DRIVE_CLIENT_ID);
            params.put("client_secret", DRIVE_CLIENT_SECRET);
            params.put("grant_type", GRANT_TYPE_REFRESH);
            params.put("refresh_token", driveRefreshToken);
            JSONObject object = HttpWorker.sendPostRequest(DRIVE_TOKEN_URL, params);
            this.accessToken = object.getString("access_token");
            this.tokenExpires = object.getLong("expires_in") * 1000 + System.currentTimeMillis();
        } catch (Exception e) {
            Logger.error("Exception in refreshAccessToken", e);
        }
    }

    private static OAuth2UserData parseDriveData(JSONObject jsonObject) throws JSONException {
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
        return pageToken != null && !pageToken.isEmpty();
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
                        DRIVE_CLOUD_ID,
                        obj.getString("id"),
                        obj.getString("title"),
                        obj.getString("downloadUrl") + "&oauth_token=" + accessToken,
                        tokenExpires
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
