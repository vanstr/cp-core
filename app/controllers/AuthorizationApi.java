package controllers;

import clouds.Dropbox;
import clouds.GDrive;
import clouds.OAuth2UserData;
import com.fasterxml.jackson.databind.JsonNode;
import models.UserEntity;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.11.9
 * Time: 20:48
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationApi extends BaseController {

    // { "login": "user", "password" : "changeme" }
    public static Result login() {
        JsonNode receivedJson = request().body().asJson();
        String login = receivedJson.findPath("login").asText();
        String password = receivedJson.findPath("password").asText();

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("login", login);
        fieldMap.put("password", password);
        List<UserEntity> list = UserEntity.getUsersByFields(fieldMap);
        if (list != null && list.size() > 0) {
            UserEntity userEntity = list.get(0);
            session().clear();
            session("user", userEntity.id.toString());
            return returnInJsonOk(userEntity);
        }

        flash("errorMessage", "Failed to log in");
        return badRequest("user not found");
    }

    public static Result logout() {
        session().clear();
        return ok();
    }

    // { "login": "user", "password" : "changeme" }
    public static Result registerUser() {
        JsonNode receivedJson = request().body().asJson();
        String login = receivedJson.findPath("login").asText();
        String password = receivedJson.findPath("password").asText();

        UserEntity checingkUserEntity = UserEntity.getUserByField("login", login);
        if (checingkUserEntity != null){
            return badRequest("Login already exists");
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.login = login;
        newUserEntity.password = password;
        newUserEntity.save();

        return returnInJsonOk(newUserEntity);
    }

    public static Result driveAuthComplete(String code) {

        GDrive gDrive = new GDrive(null, null, null);
        OAuth2UserData oAuth2UserData = gDrive.retrieveAccessToken(code);

        UserEntity userEntity = UserEntity.getUserByField("google_email", oAuth2UserData.getUniqueCloudId());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.driveAccessToken = oAuth2UserData.getAccessToken();
            userEntity.driveRefreshToken = oAuth2UserData.getRefreshToken();
            userEntity.googleEmail = oAuth2UserData.getUniqueCloudId();
            userEntity.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();
            userEntity.save();
        } else {
            userEntity.driveAccessToken = oAuth2UserData.getAccessToken();
            userEntity.driveRefreshToken = oAuth2UserData.getRefreshToken();
            userEntity.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();
            userEntity.update();
        }
        String userId = session("user");
        if (userId == null) {
            session().clear();
            session("user", userEntity.id.toString());
        }else if(Long.parseLong(userId) != userEntity.id){
            //TODO redirect with error message
            return badRequest("This GDrive account is already used");
        }

        return redirect("/");
    }


    public static Result dropboxAuthComplete(String code) {
        Dropbox dropbox = new Dropbox();
        OAuth2UserData oAuth2UserData = dropbox.retrieveAccessToken(code);

        UserEntity userEntity = UserEntity.getUserByField("dropbox_uid", oAuth2UserData.getUniqueCloudId());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.dropboxAccessKey = oAuth2UserData.getAccessToken();
            userEntity.dropboxUid = oAuth2UserData.getUniqueCloudId();
            userEntity.save();
        } else {
            userEntity.dropboxAccessKey = oAuth2UserData.getAccessToken();
            userEntity.update();
        }
        String userId = session("user");
        if (userId == null) {
            session().clear();
            session("user", userEntity.id.toString());
        }else if(Long.parseLong(userId) != userEntity.id){
            //TODO redirect with error message
            return badRequest("This Dropbox account is already used");
        }

        return redirect("/");
    }

}
