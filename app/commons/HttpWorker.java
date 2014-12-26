package commons;

import commons.exceptions.UnauthorizedAccessException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import play.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 1/18/14
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpWorker {

    public static JSONObject sendPostRequest(String url, Map<String, String> params) {
        URL obj = null;
        JSONObject object = null;
        try {
            String urlParameters = "";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlParameters += entry.getKey() + "=" + entry.getValue() + "&";
            }

            obj = new URL(url + "?" + urlParameters);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            if (con.getResponseCode() == 401) {
                throw new UnauthorizedAccessException();
            } else if (con.getResponseCode() < 200 || con.getResponseCode() >= 300) {
                return null;
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            object = new JSONObject(response.toString());
        } catch (IOException e) {
            Logger.error("Exception in sendPostRequest", e);
        }
        return object;
    }

    public static JSONObject sendGetRequest(String url) {
        HttpGet request = new HttpGet(url);

        JSONObject object = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 401) {
                throw new UnauthorizedAccessException();
            } else if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                return null;
            }
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            object = new JSONObject(result.toString());
        } catch (IOException e) {
            Logger.error("Exception:" + e);
        }
        return object;
    }
}
