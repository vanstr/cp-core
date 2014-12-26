package clouds.oauth;

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
    private Integer expiresIn;

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

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }
}
