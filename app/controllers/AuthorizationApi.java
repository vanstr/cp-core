package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import commons.PasswordService;
import controllers.commons.BaseController;
import controllers.commons.Secured;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorizationApi extends BaseController {

    public static final int MIN_LOGIN_LENGTH = 4;
    public static final int MIN_PASSWORD_LENGTH = 4;

    // { "login": "user", "password" : "changeme" }
    public static Result login() {
        Logger.debug("login");
        JsonNode receivedJson = request().body().asJson();
        String login = receivedJson.findPath("login").asText();
        String password = receivedJson.findPath("password").asText();
        Logger.debug("json:" + receivedJson);

        String hashedPassword = PasswordService.encrypt(password);

        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("login", login);
        fieldMap.put("password", hashedPassword);
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

    @Security.Authenticated(Secured.class)
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


        if (login.length() < MIN_LOGIN_LENGTH) {
            return badRequest("Login too short");
        }
        if (!isLoginFree(login)) {
            return badRequest("Login already exists");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return badRequest("Password too short");
        }

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setLogin(login);
        String hashedPassword = PasswordService.encrypt(password);
        newUserEntity.setPassword(hashedPassword);
        newUserEntity.save();

        return returnInJsonCreated(newUserEntity);
    }

    @Security.Authenticated(Secured.class)
    public static Result removeAccount() {
        Logger.debug("removeAccount");
        long userId = Long.parseLong(session("userId"));

        PlayListEntity.deletePlayListsByUserId(userId);
        SongEntity.deleteSongsByUserId(userId);
        UserEntity.deleteUserById(userId);

        session().clear();

        return ok();
    }


    @Security.Authenticated(Secured.class)
    public static Result getUser() {
        Logger.debug("getUser");
        UserEntity userEntity = getUserFromSession();

        return returnInJsonCreated(userEntity);
    }

    @Security.Authenticated(Secured.class)
    public static Result updateUser() {
        return play.mvc.Results.TODO;
    }


    // { "password": "changeme", "new_password" : "new_password" }
    @Security.Authenticated(Secured.class)
    public static Result updatePassword() {
        Logger.debug("updatePassword");
        JsonNode receivedJson = request().body().asJson();
        String currentPassword = receivedJson.findPath("password").asText();
        String newPassword = receivedJson.findPath("new_password").asText();

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            return badRequest("New password too short");
        }

        UserEntity user = getUserFromSession();
        String hashedCurrentPassword = PasswordService.encrypt(currentPassword);
        String pwd = user.getPassword();
        if (pwd != null && pwd.equals(hashedCurrentPassword)) {
            String hashedNewPassword = PasswordService.encrypt(newPassword);
            user.setPassword(hashedNewPassword);
            user.save();
            return ok();
        } else {
            return badRequest("Wrong current password");
        }

    }


    // { "password": "changeme", "login" : "myLogin" }
    @Security.Authenticated(Secured.class)
    public static Result addLoginAndPasswordForExistingUser() {
        Logger.debug("addLoginAndPasswordForExistingUser");
        JsonNode receivedJson = request().body().asJson();
        String password = receivedJson.findPath("password").asText();
        String login = receivedJson.findPath("login").asText();

        UserEntity currentUser = getUserFromSession();
        if(currentUser.getLogin() != null ){ // allowed only for users, who doesn't have login
            return badRequest("This user can't execute this operation");
        }

        if (login.length() < MIN_LOGIN_LENGTH) {
            return badRequest("Login too short");
        }
        if (!isLoginFree(login)) {
            return badRequest("Login already exists");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return badRequest("Password too short");
        }

        currentUser.setLogin(login);
        String hashedPassword = PasswordService.encrypt(password);
        currentUser.setPassword(hashedPassword);
        currentUser.save();

        return returnInJsonCreated(currentUser);
    }

    public static Boolean isLoginFree(String login) {
        UserEntity user = UserEntity.getUserByField("login", login);
        return user == null;
    }
}
