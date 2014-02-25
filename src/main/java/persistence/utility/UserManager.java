package persistence.utility;

import persistence.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * Process CURD activities relatively User table
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 18:26
 * User table manager
 */
public class UserManager extends EntityManager<UserEntity> {

    public static final String table =  "UserEntity";

    public void finalize(){
        super.finalize();
    }

    public boolean updateUser(final UserEntity user) {
        return updateEntity(user);
    }


    public List<UserEntity> getUsersByFields(Map<String, Object> fields) {
        return getEntitiesByFields(fields, table);
    }

    public boolean addUser(final UserEntity user) {
        return addEntity(user);
    }


    public boolean deleteUsersByIDs(final List<Long> ids) {
        return deleteEntityByIDs(ids, table);
    }

    public UserEntity getUserById(long id) {
        return getEntityById(UserEntity.class, id);
    }

}
