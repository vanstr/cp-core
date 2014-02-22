package persistence.manage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.22.2
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class TransactionWrapper {

    final static Logger logger = LoggerFactory.getLogger(UserManager.class);

    public boolean run(Session session, AbstractExecutor executor) {

        Transaction tx = null;

        boolean res = false;

        try {

            tx = session.beginTransaction();
            tx.setTimeout(5);

            executor.execute();

            tx.commit();
            res = true;

        } catch (RuntimeException e) {
            e.printStackTrace();

            doRollback(tx);

            res = false;
        }

        return res;


    }

    private void doRollback(Transaction tx) {
        try {
            logger.error("Couldn’t commit");
            tx.rollback();
        } catch (RuntimeException e) {
            logger.error("Couldn’t roll back transaction", e);
            e.printStackTrace();
        }
    }

}