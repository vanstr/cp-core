package persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.19.2
 * Time: 21:12
 * To change this template use File | Settings | File Templates.
 */
public class SongManager {
    final static Logger logger = LoggerFactory.getLogger(UserManager.class);

    private Session session = null;

    public SongManager() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public Session getSession() {
        return session;
    }

    public void finalize(){
        session.close();
    }

    public SongEntity getSongById(long id) {

        SongEntity song = (SongEntity) session.load(SongEntity.class, id);

        return song;
    }

    public boolean addSong(SongEntity song) {

        boolean res = false;

        Transaction ta = null;
        try {
            ta = session.beginTransaction();
            session.save(song);
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

    public boolean updateSong(SongEntity song) {

        boolean res = false;

        Transaction ta = null;
        try {
            ta = session.beginTransaction();
            session.update(song);
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
}
