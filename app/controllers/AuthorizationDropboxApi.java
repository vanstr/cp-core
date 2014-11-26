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


    public static Result getAuthUrl() {
        String clientId = SystemProperty.DROPBOX_APP_KEY;
        String redirectUrl = SystemProperty.DROPBOX_REDIRECT_URI;
        String url = SystemProperty.DROPBOX_AUTH_URL + "?client_id=" + clientId + "&response_type=code&redirect_uri=" + redirectUrl;
        return ok(url);
    }

    public static Result authComplete(String code) {
        Logger.info("authComplete");
        if (!code.isEmpty()) {
            if (isLoggedIn()) {
                addDropboxCredential(code);
            } else {
                loginWithDropbox(code);
            }
        }
        // TODO add to properties, how to support mobile apps
        return redirect(SystemProperty.DROPBOX_FINISHED_URL);
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
