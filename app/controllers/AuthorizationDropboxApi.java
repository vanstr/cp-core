package controllers;

import clouds.Dropbox;
import clouds.OAuth2UserData;
import commons.SystemProperty;
import controllers.commons.BaseController;
import controllers.commons.Secured;
import models.UserEntity;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;


public class AuthorizationDropboxApi extends BaseController {


    public static Result getAuthorizationUrl() {
        String clientId = SystemProperty.DROPBOX_APP_KEY;
        String redirectUrl = SystemProperty.DROPBOX_REDIRECT_AUTHORISED;
        String url = SystemProperty.DROPBOX_AUTH_URL + "?client_id=" + clientId + "&response_type=code&redirect_uri=" + redirectUrl;
        return ok(url);
    }

    public static Result getAddingUrl() {
        String clientId = SystemProperty.DROPBOX_APP_KEY;
        String redirectUrl = SystemProperty.DROPBOX_REDIRECT_ADDED;
        String url = SystemProperty.DROPBOX_AUTH_URL + "?client_id=" + clientId + "&response_type=code&redirect_uri=" + redirectUrl;
        return ok(url);
    }

    public static Result authComplete(String code) {
        Logger.info("authComplete");
        if (!code.isEmpty()) {
            loginWithDropbox(code);
        }
        return redirect(SystemProperty.WEB_APP_HOST);
    }


    @Security.Authenticated(Secured.class)
    public static Result addingComplete(String code) {
        Logger.info("addingComplete");
        String message = "";
        if (!code.isEmpty()) {
            try{
                addDropboxCredential(code);
            }catch (Exception ignored){
                message = "/#/?message=failed to add account&type=error";
            }
        }
        return redirect(SystemProperty.WEB_APP_HOST + message);
    }


    @Security.Authenticated(Secured.class)
    public static Result removeAccount() {
        Long userId = Long.valueOf(session("userId"));
        UserEntity userEntity = UserEntity.getUserById(userId);
        userEntity.setDropboxAccessKey(null);
        userEntity.setDropboxUid(null);
        userEntity.update();

        // TODO delete all songs

        return ok();
    }


    private static void loginWithDropbox(String code) {
        Logger.info("loginWithDropbox");
        Dropbox dropbox = new Dropbox();
        OAuth2UserData oAuth2UserData = dropbox.retrieveAccessToken(code);

        UserEntity userEntity = UserEntity.getUserByField("dropbox_uid", oAuth2UserData.getUniqueCloudId());
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setDropboxAccessKey(oAuth2UserData.getAccessToken());
            userEntity.setDropboxUid(oAuth2UserData.getUniqueCloudId());
            userEntity.save();
        } else {
            userEntity.setDropboxAccessKey(oAuth2UserData.getAccessToken());
            userEntity.update();
        }

        session().clear();
        session("userId", userEntity.getId().toString());
        session("username", "" + userEntity.getLogin());

    }

    private static void addDropboxCredential(String code) {
        Logger.info("addDropboxCredential");

        Dropbox drop = new Dropbox();
        OAuth2UserData oAuth2UserData = drop.retrieveAccessToken(code);

        Long userId = Long.valueOf(session("userId"));
        UserEntity user = UserEntity.getUserById(userId);

        // save accessTokens to DB
        user.setDropboxAccessKey(oAuth2UserData.getAccessToken());
        user.setDropboxUid(oAuth2UserData.getUniqueCloudId());
        user.save();
    }

}
