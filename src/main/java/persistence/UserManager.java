package persistence;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Process CURD activities relatively User table
 * UserEntity: vanstr
 * Date: 13.7.7
 * Time: 18:26
 * User table manager
 */
public class UserManager {

    private Session session = null;

    public UserManager() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public Session getSession() {
        return session;
    }

    public void finalize(){
        session.close();
    }

    public UserEntity getUserById(long id) {

        UserEntity user = (UserEntity) session.load(UserEntity.class, id);

        return user;
    }

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
                System.out.println("Couldn’t commit");
                ta.rollback();
            } catch (Exception re) {
                System.out.println("Couldn’t roll back transaction");
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
                System.out.println("Couldn’t commit");
                ta.rollback();
            } catch (Exception re) {
                System.out.println("Couldn’t roll back transaction");
            }
            finally {
                res = false;
            }
        }
        finally {
            return res;
        }
    }

    public List<UserEntity> getUserByFields(List<String> fieldNames, List<Object> fieldValues){
        if(fieldNames == null || fieldNames.size() == 0 || fieldValues == null
                || fieldValues.size() == 0 || fieldNames.size() != fieldValues.size()){
            return null;
        }
        String queryString = "from UserEntity where";
        for(String fieldName : fieldNames){
            if(fieldName.equals(fieldNames.get(0))){
                queryString += " " + fieldName + "=?";
            }else{
                queryString += " and " + fieldName + "=?";
            }
        }
        Query query = session.createQuery(queryString);
        for(int i = 0; i < fieldValues.size(); i++){
            query.setParameter(i, fieldValues.get(i));
        }
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
