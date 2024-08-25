package com.dk.lib_dk.db;


import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;
import java.util.concurrent.Callable;

public class DbUtils {

    protected static AbstractDaoSession session;

    protected static final int add = 11;
    protected static final int edit = 12;
    protected static final int delt = 13;


    //region 增删改


    public static <T> void add(Class entityClass, T data) {
        handleAUD(add, entityClass, data);
    }

    public static <T> void add(Class entityClass, List<T> data) {
        handleAUD(add, entityClass, data);
    }


    public static <T> void edit(Class entityClass, List<T> data) {
        handleAUD(edit, entityClass, data);
    }

    public static <T> void edit(Class entityClass, T data) {
        handleAUD(edit, entityClass, data);
    }

    public static <T> void delt(Class entityClass, List<T> data) {
        handleAUD(delt, entityClass, data);
    }

    public static <T> void delt(Class entityClass, T data) {
        handleAUD(delt, entityClass, data);
    }


    static <T> void handleAUD(int type, AbstractDaoSession AbstractDaoSession, Class entityClass, T data) {
        AbstractDao abstractDao = AbstractDaoSession.getDao(entityClass);
        switch (type) {
            case add:
                abstractDao.insertInTx(data);
                break;
            case edit:
                abstractDao.updateInTx(data);
                break;
            case delt:
                abstractDao.deleteInTx(data);
                break;
        }
    }

    static <T> void handleAUD(int type, Class entityClass, T data) {
        handleAUD(type, session, entityClass, data);
    }

    static <T> void handleAUD(int type, Class entityClass, List<T> data) {
        handleAUD(type, session, entityClass, data);
    }

    static <T> void handleAUD(int type, AbstractDaoSession AbstractDaoSession, Class entityClass, List<T> data) {
        AbstractDao abstractDao = AbstractDaoSession.getDao(entityClass);
        switch (type) {
            case add:
                abstractDao.insertInTx(data);
                break;
            case edit:
                abstractDao.updateInTx(data);
                break;
            case delt:
                abstractDao.deleteInTx(data);
                break;
        }
    }

    //endregion


    //region 查询
    public static <T> List<T> query(Class entityClass, Boolean isAsc, Property property, WhereCondition cond, WhereCondition... condMore) {
        return queryPage(entityClass, isAsc, property, null, null, cond, condMore);
    }

    public static <T> List<T> query(Class entityClass, WhereCondition cond, WhereCondition... condMore) {
        return query(entityClass, null, null, cond, condMore);
    }

    public static <T> List<T> queryPage(AbstractDaoSession AbstractDaoSession, Class entityClass, Boolean isAsc, Property property, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {

        QueryBuilder queryBuilder = obtinQuerBuilder(AbstractDaoSession, entityClass, isAsc, property, offset, limit, cond, condMore);
        return queryBuilder
                .build()
                .list();
    }

    protected static QueryBuilder obtinQuerBuilder(AbstractDaoSession daoSess, Class entityClass, Boolean isAsc, Property property, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        //清除一下缓存，保证数据是最新
        AbstractDao abstractDao = daoSess.getDao(entityClass);
        abstractDao.detachAll();//利用dao.detachAll方法.可以看到，此方法是清除缓存


        QueryBuilder queryBuilder = abstractDao.queryBuilder();
        queryBuilder.where(cond, condMore);
        if (null != isAsc && null != property) {
            if (isAsc)
                queryBuilder.orderAsc(property);
            else
                queryBuilder.orderDesc(property);
        }
        if (null != offset && null != limit) {
            queryBuilder.offset(offset * limit);
            queryBuilder.limit(limit);
        }
        return queryBuilder;
    }


    public static <T> List<T> queryPage(Class entityClass, Boolean isAsc, Property property, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        return queryPage(session, entityClass, isAsc, property, offset, limit, cond, condMore);
    }

    //endregion


    //region 初始化
    public static void initSession(AbstractDaoSession se) {
        session = se;
    }

    public static AbstractDaoSession getmDaoSession() {
        return session;
    }
    //endregion


    //region 事务
    public static <V> V callInTx(Callable<V> callable) throws Exception {
        return session.callInTx(callable);
    }

    public static void runInTx(Runnable runnable) {
        session.runInTx(runnable);
    }
    //endregion

    //region 统计
    public static long count(Class entityClass, WhereCondition cond, WhereCondition... condMore) {
        QueryBuilder queryBuilder = obtinQuerBuilder(session, entityClass, null, null, null, null, cond, condMore);
        return queryBuilder.count();
    }
    //endregion
}
