package controllers.commons;

import com.fasterxml.jackson.databind.JsonNode;
import models.UserEntity;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Jsonp.jsonp;

public class BaseController extends Controller {

    protected static boolean isLoggedIn(){
        return session().get("userId") != null && !session().get("userId").isEmpty();
    }

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

    public static UserEntity getUserFromSession() {
        Long userId = Long.parseLong(session("userId"));
        UserEntity userEntity = UserEntity.getUserById(userId);
        return userEntity;
    }

}
