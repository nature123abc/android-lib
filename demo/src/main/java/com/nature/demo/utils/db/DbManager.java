
package com.nature.demo.utils.db;

import android.content.Context;

import com.dk.lib_dk.db.DbUtils;
import com.nature.demo.dao.DaoMaster;
import com.nature.demo.dao.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

public class DbManager {

    private static DbManager mDbManager;
    static public DaoSession mDaoSession = null;
    public static String common_db = "";//系统数据库路径
    public static final String mm = ".mm";//数据库自身加密文件
    public void initDb(Context context, String dbName) {
        initDb1(context, dbName);
    }


    public void initDb1(Context context, String dbName) {
        DaoMaster.OpenHelper mHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        Database db = mHelper.getWritableDb();
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession(IdentityScopeType.None);//每次都是使用数据库的数据，不使用session缓存
        DbUtils.initSession(mDaoSession);
    }




    public static DbManager getInstance() {
        if (null == mDbManager) {
            synchronized (DbManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbManager();
                }
            }
        }
        return mDbManager;
    }


}


