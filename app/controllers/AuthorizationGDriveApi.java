package controllers;

import clouds.GDrive;
import clouds.OAuth2UserData;
import commons.SystemProperty;
import controllers.commons.BaseController;
import controllers.commons.Secured;
import models.UserEntity;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;


public class AuthorizationGDriveApi extends BaseController {


    public static Result getAuthUrl() {
        String clientId = SystemProperty.DRIVE_CLIENT_ID;
        String redirectUrl = SystemProperty.DRIVE_REDIRECT_URI;
        String scope = SystemProperty.DRIVE_SCOPE+"+"+SystemProperty.DRIVE_EMAIL_SCOPE;

        String url = SystemProperty.DRIVE_AUTH_URL + "?redirect_uri="+ redirectUrl +"&response_type=code&client_id="+clientId+"&scope="+scope+"&approval_prompt=force&access_type=offline";
        return ok(url);
    }

    public static Result authComplete(String code) {
        Logger.info("authComplete");
        if (!code.isEmpty()) {
            if (isLoggedIn()) {
                addGDriveCredential(code);
            } else {
                loginWithGDrive(code);
            }
        }
        // TODO add to properties, how to support mobile apps
        return redirect(SystemProperty.DRIVE_FINISHED_URL);
    }


    @Security.Authenticated(Secured.class)
    public static Result removeAccount() {
        Long userId = Long.valueOf(session("userId"));
        UserEntity user = UserEntity.getUserById(userId);
        user.setDriveAccessToken(null);
        user.setDriveRefreshToken(null);
        user.setDriveTokenExpires(null);
        user.setGoogleEmail(null);
        user.update();

        // TODO delete all songs

        return ok();
    }


    private static void loginWithGDrive(String code) {
        Logger.info("loginWithGDrive");

        GDrive gDrive = new GDrive(null, null, null);
        OAuth2UserData oAuth2UserData = gDrive.retrieveAccessToken(code);

        UserEntity userEntity = UserEntity.getUserByField("google_email", oAuth2UserData.getUniqueCloudId());
        if(userEntity == null ){
            userEntity = new UserEntity();
            userEntity.setDriveAccessToken(oAuth2UserData.getAccessToken());
            userEntity.setDriveRefreshToken(oAuth2UserData.getRefreshToken());
            userEntity.setGoogleEmail(oAuth2UserData.getUniqueCloudId());
            userEntity.setDriveTokenExpires(oAuth2UserData.getExpiresIn()*1000 + System.currentTimeMillis());
            userEntity.save();
        }else{

            userEntity.setDriveAccessToken(oAuth2UserData.getAccessToken());
            userEntity.setDriveRefreshToken(oAuth2UserData.getRefreshToken());
            userEntity.setDriveTokenExpires(oAuth2UserData.getExpiresIn()*1000 + System.currentTimeMillis());
            userEntity.update();
        }

        session().clear();
        session("userId", userEntity.getId().toString());
        session("username", "" + userEntity.getLogin());

    }

    private static void addGDriveCredential(String code) {
        Logger.info("addGDriveCredential");

        Long userId = Long.valueOf(session("userId"));
        UserEntity user = UserEntity.getUserById(userId);

        // retrive AccessToken
        GDrive gDrive = new GDrive(null, null, null);
        OAuth2UserData credentials = gDrive.retrieveAccessToken(code);
        String accessToken = credentials.getAccessToken();
        String refreshToken = credentials.getRefreshToken();

        // save accessTokens to DB
        user.setDriveAccessToken(accessToken);
        user.setDriveRefreshToken(refreshToken);
        user.setDriveTokenExpires(credentials.getExpiresIn()*1000 + System.currentTimeMillis());
        user.setGoogleEmail(credentials.getUniqueCloudId());
        user.save();
    }

}
