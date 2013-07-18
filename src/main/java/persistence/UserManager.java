package persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;

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


}
