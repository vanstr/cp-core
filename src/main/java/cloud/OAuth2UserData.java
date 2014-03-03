package cloud;

import commons.HttpWorker;
import org.codehaus.jackson.map.util.JSONPObject;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 3/3/14
 * Time: 10:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class OAuth2UserData {
    private String accessToken;
    private String refreshToken;
    private String uniqueCloudId;

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

    public String getUniqueCloudId() {
        return uniqueCloudId;
    }

    public void setUniqueCloudId(String uniqueCloudId) {
        this.uniqueCloudId = uniqueCloudId;
    }

    public static OAuth2UserData parseDropboxData(JSONObject jsonObject){
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.accessToken = jsonObject.getString("access_token");
        oAuth2UserData.uniqueCloudId = jsonObject.getString("uid");
        return oAuth2UserData;
    }

    public static OAuth2UserData parseDriveData(JSONObject jsonObject){
        OAuth2UserData oAuth2UserData = new OAuth2UserData();
        oAuth2UserData.accessToken = jsonObject.getString("access_token");
        oAuth2UserData.refreshToken = jsonObject.getString("refresh_token");
        JSONObject object = HttpWorker.sendGetRequest("https://www.googleapis.com/userinfo/email?alt=json&oauth_token="
                + oAuth2UserData.accessToken);
        String email = object.getJSONObject("data").get("email").toString();
        oAuth2UserData.uniqueCloudId = email;
        return oAuth2UserData;
    }
}
