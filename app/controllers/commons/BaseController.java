package controllers.commons;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Jsonp.jsonp;

/**
 * Created by imi on 14.09.2014..
 */
public class BaseController extends Controller {

    public static Result returnOk(String callback, JsonNode jsonOutput) {
        if (callback == null) {
            return ok(jsonOutput);
        }
        return ok(jsonp(callback, jsonOutput));
    }

    public static Result returnInJsonOk(Object obj) {
        if (obj == null) {
          return ok();
        }
        JsonNode json = Json.toJson(obj);

        return ok(json);
    }

    public static Result returnInJsonCreated(Object obj) {

        JsonNode json = Json.toJson(obj);

        return created(json);
    }

    public static Result returnInJsonOk(String callback, Object obj) {

        JsonNode json = Json.toJson(obj);
        if (callback == null) {
            return ok(json);
        }
        return ok(jsonp(callback, json));
    }

}
