package persistence.manage;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.HibernateUtil;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.22.2
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class EntityManager<T> {

    final static Logger logger = LoggerFactory.getLogger(EntityManager.class);

    private Session session;

    private static int openedSession = 0;
    private static int closedSession = 0;

    public static String getSessionStatistic() {
        //HibernateUtil.getSessionFactory().getStatistics().
        return "Sessions - opened:" + openedSession + " closed:" + closedSession;
    }

    TransactionWrapper transactionWrapper = new TransactionWrapper();

    public void getSession() {

        if (session == null) {
            openedSession++;
            logger.info("Create new session");
            session = HibernateUtil.getSessionFactory().openSession();
        }else{
            logger.info("Sesion not created");
        }
    }

    public void closeSession(Session session) {
        if (session != null) {
            closedSession++;
            session.close();
        }
    }

    public void finalize() {
        closeSession(session);
    }

    public T getEntityById(Class<T> EntityClass, long id) {
        getSession();

        T entity = (T) session.load(EntityClass, id);

        return entity;
    }

    public List<T> getEntitiesByFields(Map<String, Object> fields, String table) {

        List<T> list = null;
        if (fields != null && fields.size() > 0) {

            String queryString = "from " + table + " where";
            String andSeparator = " ";
            for (String key : fields.keySet()) {
                queryString += andSeparator + key + "=:" + key;
                andSeparator = " AND ";
            }

            getSession();
            Query query = session.createQuery(queryString);
            query.setProperties(fields);
            list = query.list();
            if (list.size() == 0) {
                list = null;
            }
            //closeSession(session);
            logger.debug("Search entities in " + table + " by fields, hql:" + query.getQueryString());
        }

        return list;
    }

    public boolean deleteEntityByIDs(final List<Long> ids, final String table) {
        getSession();

        return transactionWrapper.run(session, new AbstractExecutor() {
            @Override
            public void execute() {
                String queryStatement = "delete from " + table;

                String commaSeparator = " ";
                String idList = "";
                for (Long key : ids) {
                    idList += commaSeparator + key;
                    commaSeparator = ",";
                }

                queryStatement += " where id in (" + idList + ") ";
                Query query = session.createQuery(queryStatement);
                query.executeUpdate();

                logger.debug("Deleted entities, hql:" + query.getQueryString());
            }
        });
    }


    public boolean updateEntity(final T entity) {
        getSession();

        return transactionWrapper.run(session, new AbstractExecutor() {
            @Override
            public void execute() {
                session.update(entity);
            }
        });
    }

    public boolean addEntity(final T entity) {
        getSession();

        return transactionWrapper.run(session, new AbstractExecutor() {
            @Override
            public void execute() {
                session.save(entity);
            }
        });
    }


}
