package com.dk.lib_dk.db.encry;


import com.dk.error.CommonException;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by hq on 2017/12/27.
 *
 * @描述：数据库加密，解密数据库
 */
public class DbEncryptUtils {
    /**
     * 加密数据库
     * @param encryptedName 加密后的数据库名称
     * @param decryptedName 要加密的数据库名称
     * @param key 密码
     */
    /**
     * 使用第三方库加密数据库
     *
     * @param encryptedName 加密后的数据库名称
     * @param decryptedName 要加密的数据库名称
     * @param key           密码
     */
    public static boolean encrypt(String decryptedName, String encryptedName, String key) {
        SQLiteDatabase encrypteddatabase = null;
        SQLiteDatabase database = null;
        try {
            File databaseFile = new File(decryptedName);
            database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "", null);//打开要加密的数据库
            /*String passwordString = "1234"; //只能对已加密的数据库修改密码，且无法直接修改为“”或null的密码
            database.changePassword(passwordString.toCharArray());*/
            File encrypteddatabaseFile = new File(encryptedName);//新建加密后的数据库文件
            String[] strings = encryptedName.split("\\.")[0].split("/");
            String temp = strings[strings.length - 1];
            //deleteDatabase(SDcardPath + encryptedName);
            //连接到加密后的数据库，并设置密码
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' as " + temp + " KEY '" + key + "';", encrypteddatabaseFile.getPath()));
            //输出要加密的数据库表和数据到加密后的数据库文件中
            database.rawExecSQL("SELECT sqlcipher_export('" + temp + "');");
            //断开同加密后的数据库的连接
            database.rawExecSQL("DETACH DATABASE " + temp + ";");
            //打开加密后的数据库，测试数据库是否加密成功
            encrypteddatabase = SQLiteDatabase.openOrCreateDatabase(encrypteddatabaseFile, key, null);
            //encrypteddatabase.setVersion(database.getVersion());
            encrypteddatabase.close();//关闭数据库
            database.close();
            return true;
        } catch (Exception e) {
            if (null != encrypteddatabase) {
                encrypteddatabase.close();
            }
            if (null != database) {
                database.close();
            }
            throw new CommonException(e.getMessage());
        }

    }

    /**
     * 解密数据库
     *
     * @param encryptedName 要解密的数据库名称
     * @param decryptedName 解密后的数据库名称
     * @param key           密码
     */
    public static boolean decrypt(String encryptedName, String decryptedName, String key) {
        SQLiteDatabase decrypteddatabase = null;
        SQLiteDatabase database = null;
        try {
            File databaseFile = new File(encryptedName);
            database = SQLiteDatabase.openOrCreateDatabase(databaseFile, key, null);

            File decrypteddatabaseFile = new File(decryptedName);
            //deleteDatabase(SDcardPath + decryptedName);
            String[] strings = decryptedName.split("\\.")[0].split("/");
            String temp = strings[strings.length - 1];
            //连接到解密后的数据库，并设置密码为空
            database.rawExecSQL(String.format("ATTACH DATABASE '%s' as " + temp + " KEY '';", decrypteddatabaseFile.getAbsolutePath()));
            database.rawExecSQL("SELECT sqlcipher_export('" + temp + "');");
            database.rawExecSQL("DETACH DATABASE " + temp + ";");

            decrypteddatabase = SQLiteDatabase.openOrCreateDatabase(decrypteddatabaseFile, "", null);
            //decrypteddatabase.setVersion(database.getVersion());
            decrypteddatabase.close();

            database.close();
            return true;
        } catch (Exception e) {
            if (null != decrypteddatabase) {
                decrypteddatabase.close();
            }
            if (null != database) {
                database.close();
            }
            e.printStackTrace();
            throw new CommonException(e.getMessage());

        }
    }


}
