package persistence;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Process CURD activities relatively User table
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 18:26
 * User table manager
 */
public class UserManager {

    final static Logger logger = LoggerFactory.getLogger(UserManager.class);

    private Session session = null;

    public UserManager() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public Session getSession() {
        return session;
    }

    public void finalize(){
        if(session.getTransaction() != null && session.getTransaction().isActive()){
            session.getTransaction().commit();
        }
        session.flush();
        session.clear();
        session.close();
    }

    public UserEntity getUserById(long id) {

        UserEntity user = (UserEntity) session.load(UserEntity.class, id);

        return user;
    }

    public Long addUser(UserEntity user) {

        Long userId;
        try {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            userId = user.getId();
        } catch (RuntimeException e) {
            try {
                logger.error("Couldn’t commit");
                session.getTransaction().rollback();
            } catch (Exception re) {
                logger.error("Couldn’t roll back transaction");
            }
            finally {
                userId = null;
            }
        }

        return userId;

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
}
