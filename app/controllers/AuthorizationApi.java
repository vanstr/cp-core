package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.commons.BaseController;
import models.PlayListEntity;
import models.UserEntity;
import play.Logger;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorizationApi extends BaseController {

    // { "login": "user", "password" : "changeme" }
    public static Result login() {
        Logger.debug("login");
        JsonNode receivedJson = request().body().asJson();
        String login = receivedJson.findPath("login").asText();
        String password = receivedJson.findPath("password").asText();
        Logger.debug("json:" + receivedJson);
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("login", login);
        fieldMap.put("password", password);
        List<UserEntity> list = UserEntity.getUsersByFields(fieldMap);
        if (list != null && list.size() > 0) {
            UserEntity userEntity = list.get(0);
            session().clear();
            session("userId", userEntity.getId().toString());
            session("username", userEntity.getLogin());
            return returnInJsonOk(userEntity);
        }

        flash("errorMessage", "Failed to log in");
        return badRequest("user not found");
    }

    public static Result logout() {
        Logger.debug("logout");
        session().clear();
        return ok();
    }

    // { "login": "user", "password" : "changeme" }
    public static Result registerUser() {
        Logger.debug("registerUser");
        JsonNode receivedJson = request().body().asJson();
        String login = receivedJson.findPath("login").asText();
        String password = receivedJson.findPath("password").asText();

        UserEntity checingkUserEntity = UserEntity.getUserByField("login", login);
        if (checingkUserEntity != null){
            return badRequest("Login already exists");
        }
        if( password.length() < 4 ){
            return badRequest("Password too short");
        }
        
        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setLogin(login);
        newUserEntity.setPassword(password);
        newUserEntity.save();

        return returnInJsonCreated(newUserEntity);
    }

    public static Result removeAccount(){
        Logger.debug("removeAccount");
        long userId = Long.parseLong(session("userId"));

        // TODO delete songs, playlists, user
        PlayListEntity.deletePlayListByUser(userId);
        UserEntity.deleteUserById(userId);

        session().clear();

        return ok();
    }


    public static Result getUser() {
        Logger.debug("getUser");
        Long userId = Long.parseLong(session("userId"));
        UserEntity userEntity = UserEntity.getUserById(userId);

        return returnInJsonCreated(userEntity);
    }

    public static Result updateUser() {
        return play.mvc.Results.TODO;
    }
}
