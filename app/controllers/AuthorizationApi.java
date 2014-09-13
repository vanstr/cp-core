package controllers;

import clouds.Dropbox;
import clouds.GDrive;
import clouds.OAuth2UserData;
import models.UserEntity;
import play.mvc.Controller;

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
public class AuthorizationApi extends Controller {

  public Long login(String login, String password) {
    Long result = null;

    Map<String, Object> fieldMap = new HashMap<String, Object>();
    fieldMap.put("login", login);
    fieldMap.put("password", password);
    List<UserEntity> list = UserEntity.getUsersByFields(fieldMap);
    if (list != null && list.size() > 0) {
      result = list.get(0).id;
    }
    return result;
  }


  public Boolean registerUser(String login, String password) {

    UserEntity newUser = new UserEntity();
    newUser.login = login;
    newUser.password = password;
    newUser.save();

    return newUser.id > 0;
  }

  /**
   * @return true - user has provided access to app and access tokens saved.
   * false - error occurred
   */
  public Boolean retrieveDropboxCredentials(Long userId, String code) {
    Boolean result = true; // TODO


    // Work with dropbox service, start session
    Dropbox drop = new Dropbox();

    OAuth2UserData oAuth2UserData = drop.retrieveAccessToken(code);
    // get requestTokens from db
    UserEntity user = UserEntity.getUserById(userId);
    if (user == null) {
      return false;
    }

    // save accessTokens to DB
    user.dropboxAccessKey = oAuth2UserData.getAccessToken();
    user.dropboxUid = oAuth2UserData.getUniqueCloudId();
    user.update();

    return result;
  }


  public Boolean retrieveGDriveCredentials(Long userId, String code) {
    Boolean result = true; // TODO

    GDrive gDrive = new GDrive(null, null, null);

    UserEntity user = UserEntity.getUserById(userId);
    if (user == null) {
      return false;
    }
    // retrive AccessToken
    OAuth2UserData credentials = gDrive.retrieveAccessToken(code);
    String accessToken = credentials.getAccessToken();
    String refreshToken = credentials.getRefreshToken();

    // save accessTokens to DB
    user.driveAccessToken = accessToken;
    user.driveRefreshToken = refreshToken;
    user.driveTokenExpires = credentials.getExpiresIn() * 1000 + System.currentTimeMillis();
    user.googleEmail = credentials.getUniqueCloudId();
    user.update();

    return result;
  }


  public Boolean removeDropboxAcoount(Long userId) {
    Boolean result = true; // TODO

    UserEntity user = UserEntity.getUserById(userId);
    if (user == null) {
      return false;
    }

    user.dropboxAccessKey = null;
    user.dropboxUid = null;
    user.update();

    return result;
  }


  public Boolean removeGDriveAccount(Long userId) {
    Boolean result = true; // TODO

    UserEntity user = UserEntity.getUserById(userId);
    if (user == null) {
      return false;
    }

    user.driveAccessToken = null;
    user.driveRefreshToken = null;
    user.driveTokenExpires = null;
    user.googleEmail = null;
    user.update();

    return result;
  }


  public Long authorizeWithDrive(String code) {

    GDrive gDrive = new GDrive(null, null, null);
    OAuth2UserData oAuth2UserData = gDrive.retrieveAccessToken(code);

    UserEntity user = UserEntity.getUserByField("google_email", oAuth2UserData.getUniqueCloudId());
    if (user == null) {
      user = new UserEntity();
      user.driveAccessToken = oAuth2UserData.getAccessToken();
      user.driveRefreshToken = oAuth2UserData.getRefreshToken();
      user.googleEmail = oAuth2UserData.getUniqueCloudId();
      user.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();
      user.save();
    }
    else {

      user.driveAccessToken = oAuth2UserData.getAccessToken();
      user.driveRefreshToken = oAuth2UserData.getRefreshToken();
      user.driveTokenExpires = oAuth2UserData.getExpiresIn() * 1000 + System.currentTimeMillis();

      user.update();
    }

    return user.id;
  }


  public Long authorizeWithDropbox(String code) {

    Dropbox dropbox = new Dropbox();
    OAuth2UserData oAuth2UserData = dropbox.retrieveAccessToken(code);

    UserEntity user = UserEntity.getUserByField("dropbox_uid", oAuth2UserData.getUniqueCloudId());
    if (user == null) {
      user = new UserEntity();
      user.dropboxAccessKey = oAuth2UserData.getAccessToken();
      user.dropboxUid = oAuth2UserData.getUniqueCloudId();
      user.save();
    }
    else {
      user.dropboxAccessKey = oAuth2UserData.getAccessToken();
      user.update();
    }

    return user.id;
  }
}
