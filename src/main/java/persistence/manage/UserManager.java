package persistence.manage;

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



    /*
    public boolean addUser(UserEntity user) {

        boolean res = false;

        Transaction ta = null;
        try {
            ta = session.beginTransaction();
            session.save(user);
            ta.commit();
            res = true;
        } catch (RuntimeException e) {
            try {
                logger.error("Couldn’t commit");
                ta.rollback();
            } catch (Exception re) {
                logger.error("Couldn’t roll back transaction");
            }
            finally {
                res = false;
            }
        }
        finally {
            return res;
        }

    }

    public boolean updateUser(UserEntity user) {

        boolean res = false;

        Transaction ta = null;
        try {
            ta = session.beginTransaction();
            session.update(user);
            ta.commit();
            res = true;
        } catch (RuntimeException e) {
            try {
                logger.error("Couldn’t commit");
                ta.rollback();
            } catch (Exception re) {
                logger.error("Couldn’t roll back transaction");
            }
            finally {
                res = false;
            }
        }
        finally {
            return res;
        }
    }

    public List<UserEntity> getUsersByFields(Map<String, Object> fields){
        if(fields == null || fields.size() == 0){
            return null;
        }
        String queryString = "from UserEntity where";
        boolean first = true;
        for(String key : fields.keySet()){
            if(first){
                queryString += " ";
                first = false;
            }else{
                queryString += " and ";
            }
            queryString += key + "=:" + key;
        }
        Query query = session.createQuery(queryString);
        query.setProperties(fields);
        List<UserEntity> list = query.list();
        return list;
    }


    public List<UserEntity> getUsersByField(String fieldName, Object fieldValue){
        String queryString = "from UserEntity where " + fieldName + "=?";
        Query query = session.createQuery(queryString);
        query.setParameter(0, fieldValue);
        List<UserEntity> list = query.list();
        return list;
    }
    */
}
