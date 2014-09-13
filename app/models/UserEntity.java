package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class UserEntity {
    @Id
    public long id;

    public String login;


    public String password;


    public String dropboxAccessKey;


    public String driveAccessToken;

    public String driveRefreshToken;


    public String googleEmail;



    public String dropboxUid;



    public Long driveTokenExpires;





    public static Model.Finder<Long, UserEntity> find = new Model.Finder<Long, UserEntity>(Long.class, UserEntity.class);

}
