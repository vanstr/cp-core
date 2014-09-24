package controllers;

import clouds.Dropbox;
import clouds.GDrive;
import clouds.OAuth2UserData;
import com.fasterxml.jackson.databind.JsonNode;
import models.User;
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
        List<User> list = User.getUsersByFields(fieldMap);
        if (list != null && list.size() > 0) {
            User user = list.get(0);
            session().clear();
            session("user", user.id.toString());
            return returnInJsonOk(user);
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

        User checingkUser = User.getUserByField("login", login);
        if (checingkUser != null){
            return badRequest("Login already exists");
        }

        User newUser = new User();
        newUser.login = login;
        newUser.password = password;
        newUser.save();

        return returnInJsonOk(newUser);
    }

    public static Result driveAuthComplete(String code) {

        GDrive gDrive = new GDrive(null, null, null);
        OAuth2UserData oAuth2UserData = gDrive.retrieveAccessToken(code);

        User user = User.getUserByField("google_email", oAuth2UserData.getUniqueCloudId());
        if (user == null) {
            user = new User();
            user.driveAccessToken = oAuth2UserData.getAccessToken();
            user.driveRefreshToken = oAuth2UserData.getRefreshToken();
            user.googleEmail = oAuth2UserData.getUniqueCloudId();
            user.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();
            user.save();
        } else {
            user.driveAccessToken = oAuth2UserData.getAccessToken();
            user.driveRefreshToken = oAuth2UserData.getRefreshToken();
            user.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();
            user.update();
        }
        String userId = session("user");
        if (userId == null) {
            session().clear();
            session("user", user.id.toString());
        }else if(Long.parseLong(userId) != user.id){
            //TODO redirect with error message
            return badRequest("This GDrive account is already used");
        }

        return redirect("/");
    }


    public static Result dropboxAuthComplete(String code) {
        Dropbox dropbox = new Dropbox();
        OAuth2UserData oAuth2UserData = dropbox.retrieveAccessToken(code);

        User user = User.getUserByField("dropbox_uid", oAuth2UserData.getUniqueCloudId());
        if (user == null) {
            user = new User();
            user.dropboxAccessKey = oAuth2UserData.getAccessToken();
            user.dropboxUid = oAuth2UserData.getUniqueCloudId();
            user.save();
        } else {
            user.dropboxAccessKey = oAuth2UserData.getAccessToken();
            user.update();
        }
        String userId = session("user");
        if (userId == null) {
            session().clear();
            session("user", user.id.toString());
        }else if(Long.parseLong(userId) != user.id){
            //TODO redirect with error message
            return badRequest("This Dropbox account is already used");
        }

        return redirect("/");
    }

    public static Result removeDrive(){
        Long userId = Long.parseLong(session("user"));
        User user = User.getUserById(userId);
        user.driveAccessToken = null;
        user.driveRefreshToken = null;
        user.googleEmail = null;
        user.driveTokenExpires = null;
        user.update();
        return ok();
    }

    public static Result removeDropbox(){
        Long userId = Long.parseLong(session("user"));
        User user = User.getUserById(userId);
        user.dropboxAccessKey = null;
        user.dropboxUid = null;
        user.update();
        return ok();
    }
}
