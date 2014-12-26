package clouds.oauth;

import commons.HttpWorker;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/23/14
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OAuth2Communicator {

    public abstract OAuth2UserData retrieveAccessToken(String code, String redirectUrl);
    public abstract String refreshToken(String refreshToken);

    public JSONObject retrieveAccessToken(String code, String clientId,
                                              String clientSecret, String grantType,
                                              String redirectUri, String scope, String url){
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("grant_type", grantType);
        params.put("redirect_uri", redirectUri);
        if(scope != null && !"".equals(scope)){
            params.put("scope", scope);
        }
        JSONObject object = HttpWorker.sendPostRequest(url, params);
        return object;
    }
}
