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

    public void finalize(){
        session.close();
    }

    public UserEntity getUserById(long id) {

        /*
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "from UserEntity s where s.id = :userID";
        Query query = session.createQuery(hql).setParameter("userID", id);
        List<UserEntity> list = query.list();
        return list.get(0);
        */

        UserEntity user = (UserEntity) session.load(UserEntity.class, id);
        //System.out.println(user.getLogin());

        return user;
    }

    public boolean addUser() {

        /*
        UserEntity newUser = new UserEntity();
        Session session = HibernateUtil.getSessionFactory().openSession();
        org.hibernate.Transaction transaction = session.beginTransaction();
        newUser.setLogin("login");
        newUser.setPassword("pwd");
        session.save(newUser);
        transaction.commit();
        session.close();
         */
        return false;
    }

    public boolean updateUser(UserEntity user) {

        boolean res = false;

        Transaction ta = null;
        try {
            ta = session.beginTransaction();
            ta.setTimeout(5);

            session.update(user);

            ta.commit();
        } catch (RuntimeException e) {
            try {
                ta.rollback();
            } catch (RuntimeException rbe) {
                System.out.println("Couldn’t roll back transaction");
            }
            throw e;
        } finally {
            res = true;
        }

        //System.out.println(user.getLogin());

        return res;

    }


}
