package com.dk.lib_dk.db;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class DbUtilsPlus extends DbUtils {
    //region AUD
    public static <T> Observable plusAdd(Class entityClass, List<T> data) {
        Observable observable = Observable.create(emitter -> {
            add(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable plusAdd(Class entityClass, T data) {
        Observable observable = Observable.create(emitter -> {
            add(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable plusEdit(Class entityClass, List<T> data) {
        Observable observable = Observable.create(emitter -> {
            edit(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable plusEdit(Class entityClass, T data) {
        Observable observable = Observable.create(emitter -> {
            edit(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable plusDelt(Class entityClass, List<T> data) {
        Observable observable = Observable.create(emitter -> {
            delt(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable plusDelt(Class entityClass, T data) {
        Observable observable = Observable.create(emitter -> {
            delt(entityClass, data);
            emitter.onNext(true);
            emitter.onComplete();
        });
        return observable;
    }

    //endregion

    //region 查询

    public static <T> Observable<List<T>> plusQuery(Class entityClass, WhereCondition cond, WhereCondition... condMore) {
        return plusQuery(entityClass, null, null, cond, condMore);
    }

    public static <T> Observable<List<T>> plusQuery(Class entityClass, Property property, Boolean isAsc, WhereCondition cond, WhereCondition... condMore) {
        Observable observable = Observable.create(emitter -> {
            List<T> data = query(entityClass, isAsc, property, cond, condMore);
            emitter.onNext(data);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable<List<T>> plusQueryPage(Class entityClass, Property property, Boolean isAsc, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        Observable observable = Observable.create(emitter -> {
            List<T> data = queryPage(entityClass, isAsc, property, offset, limit, cond, condMore);
            emitter.onNext(data);
            emitter.onComplete();
        });
        return observable;
    }

    public static <T> Observable<List<T>> plusQueryPage(Class entityClass, Integer offset, Integer limit, WhereCondition cond, WhereCondition... condMore) {
        return plusQueryPage(entityClass, null, null, offset, limit, cond, condMore);
    }

    //endregion
}
