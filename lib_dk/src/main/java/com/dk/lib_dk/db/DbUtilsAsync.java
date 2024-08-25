package com.dk.lib_dk.db;


import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

public class DbUtilsAsync extends DbUtils {

    //region AUD

    public static <T> void add(Class entityClass, T data, AsyncOperationListener listener) {
        handle(add, entityClass, data, listener);
    }

    public static <T> void edit(Class entityClass, T data, AsyncOperationListener listener) {
        handle(edit, entityClass, data, listener);
    }


    public static <T> void delt( Class entityClass, T data, AsyncOperationListener listener) {
        handle(delt,  entityClass, data, listener);
    }



    public static <T> void handle(int type, AbstractDaoSession AbstractDaoSession, Class entityClass, T data, AsyncOperationListener listener) {
        AsyncSession async = AbstractDaoSession.startAsyncSession();
        async.setListenerMainThread(listener);
        AbstractDao abstractDao = AbstractDaoSession.getDao(entityClass);
        async.runInTx(() -> {
            switch (type) {
                case add:
                    abstractDao.insertOrReplace(data);
                    break;
                case edit:
                    abstractDao.updateInTx(data);
                    break;
                case delt:
                    abstractDao.deleteInTx(data);
                    break;
            }
        });
    }


    public static <T> void handle(int type, Class entityClass, T data, AsyncOperationListener listener) {
        handle(type, session, entityClass, data, listener);
    }

    //endregion


    //region Q
    public static <T> void queryPage(AsyncOperationListener listener, AbstractDaoSession AbstractDaoSession, Class entityClass, Boolean isAsc, Property property, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        AsyncSession async = AbstractDaoSession.startAsyncSession();
        async.setListenerMainThread(listener);
        QueryBuilder queryBuilder = obtinQuerBuilder(AbstractDaoSession, entityClass, isAsc, property, offset, limit, cond, condMore);
        async.queryList(queryBuilder.build());
    }

    public static <T> void queryPage(AsyncOperationListener listener, Class entityClass, Boolean isAsc, Property property, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        queryPage(listener, session, entityClass, isAsc, property, offset, limit, cond, condMore);
    }

    public static <T> void query(AsyncOperationListener listener, Class entityClass, Boolean isAsc, Property property, WhereCondition cond, WhereCondition... condMore) {
        queryPage(listener, entityClass, isAsc, property, null, null, cond, condMore);
    }

    public static <T> void query(AsyncOperationListener listener, Class entityClass, WhereCondition cond, WhereCondition... condMore) {
        query(listener, entityClass, null, null, cond, condMore);
    }

    //endregion

}


