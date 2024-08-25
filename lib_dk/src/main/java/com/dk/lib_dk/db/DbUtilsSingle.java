package com.dk.lib_dk.db;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class DbUtilsSingle extends DbUtils {
    //region  AUD
    public static <T> Single addData(Class entityClass, List<T> data) {
        return Single.create(emitter -> {
            try {
                add(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single addData(Class entityClass, T data) {
        return Single.create(emitter -> {
            try {
                add(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single editData(Class entityClass, List<T> data) {
        return Single.create(emitter -> {
            try {
                edit(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single editData(Class entityClass, T data) {
        return Single.create(emitter -> {
            try {
                edit(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single deltData(Class entityClass, List<T> data) {
        return Single.create(emitter -> {
            try {
                delt(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single deltData(Class entityClass, T data) {
        return Single.create(emitter -> {
            try {
                delt(entityClass, data);
                if (!emitter.isDisposed())
                    emitter.onSuccess(true);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    //endregion

    //region q
    public static <T> Single<List<T>> queryData(Class entityClass, WhereCondition cond, WhereCondition... condMore) {
        return queryData(entityClass, null, null, cond, condMore);
    }

    public static <T> Single<List<T>> queryData(Class entityClass, Property property, Boolean isAsc, WhereCondition cond, WhereCondition... condMore) {
        return Single.create(emitter -> {
            try {
                List<T> data = query(entityClass, isAsc, property, cond, condMore);
                if (!emitter.isDisposed())
                    emitter.onSuccess(data);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public static <T> Single<List<T>> queryDataPage(Class entityClass, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        return queryDataPage(entityClass, null, null, offset, limit, cond, condMore);
    }

    public static <T> Single<List<T>> queryDataPage(Class entityClass, Property property, Boolean isAsc, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        return Single.create(emitter -> {
            try {
                List<T> data = queryPage(entityClass, isAsc, property, offset, limit, cond, condMore);
                if (!emitter.isDisposed())
                    emitter.onSuccess(data);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }


    //endregion
}
