package com.dk.lib_dk.db.encry;


import com.blankj.utilcode.util.FileUtils;
import com.dk.error.CommonException;

import java.io.File;

/**
 * 解决数据库加密解密问题
 */
public class DbFileManager {

    static DbFileManager mDbManager;

    public static final String CommonDB = "CommonDB";
    public static final String Data = "Data";
    public static final String temp = ".temp";//临时解密文件
    public static final String mm = ".mm";//数据库自身加密文件
    public static final String DB = ".DB";//文件类型的数据库

    String publicDbPathName;//公共加密数据库 文件路径
    String path_sys = File.separator;//系统目录，根据APP给分配的目录
    String path_common_db = path_sys + CommonDB + File.separator;//公共数据库,新建项目之前使用的数据库
    String path_data = path_sys + Data + File.separator;//数据存放目录

    private DbFileManager() {
    }

    public static DbFileManager getInstance() {
        if (null == mDbManager) {
            synchronized (DbFileManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbFileManager();
                }
            }
        }
        return mDbManager;
    }

    /**
     * 设置公共数据库目录，
     *
     * @param sysPath 当前APP文件目录
     */
    void setPath(String sysPath) {
        path_sys = sysPath;
        path_common_db = sysPath + CommonDB + File.separator;//公共数据库,新建项目之前使用的数据库
        path_data = sysPath + Data + File.separator;//数据存放目录
    }


    /**
     * 1.加密的文件 转 加密数据库
     * 传入用户放入的数据库，判断对应加密数据库是否存在，存在则直接返回之前解压加密的数据库
     * 如果不存在对应加密数据库，则删除之前数据库文件目录，解压文件，加密数据库，并删除没有加密的数据库
     * 数据库格式  .db -> .temp ->.mm
     *
     * @param sysPath    当前APP  分配的路径
     * @param pathNameDb 用户放入的数据库 .DB
     * @return 加密后数据库路径
     */
    public String file2Db(String sysPath, String pathNameDb) {
        //1、设置各自文件路径
        setPath(sysPath);

        File fDb = new File(pathNameDb);
        //2.创建系统需要的目录
        if (!FileUtils.isFileExists(path_common_db)) {
            FileUtils.createOrExistsDir(path_common_db);
        }
        String baseNameNoExt = FileUtils.getFileNameNoExtension(fDb);//没有后缀的文件数据库文件名称
        //3.设置数据库文件目录
        String tempDB = path_common_db + baseNameNoExt + temp; //解密后的文件，没有加密的数据库  ，果奔

        String jiaMDbPathName = path_common_db + baseNameNoExt + mm; //加密数据库

        if (FileUtils.isFileExists(jiaMDbPathName)) {//存在同名文件，则不进行解密操作
            publicDbPathName = jiaMDbPathName;
            return jiaMDbPathName;
        }
        //4.不存在同名解密后文件，则需要重新拷贝；先删除文件，再新建目录
        FileUtils.delete(path_common_db);
        FileUtils.createOrExistsDir(path_common_db);

        /******************************************************************************************/
        boolean b = FileEncryptUtils.decrypt(pathNameDb, tempDB, "1234567812345678");//解密数据库成功
        if (!b){
            throw new CommonException("解密文件数据库出错");
        }
        DbEncryptUtils.encrypt(tempDB, jiaMDbPathName, "sw.12345");//把明码数据库加密
        /******************************************************************************************/
        FileUtils.delete(tempDB); //删除临时文件，没有加密的数据库
        publicDbPathName = jiaMDbPathName;
        return jiaMDbPathName;
    }


    /**
     * 2.新建作业时复制一份数据库
     * 传入作业名，将公共数据库拷贝一份到当前作业文件目录中
     *
     * @param jobName 左右名称
     * @return 复制加密数据库的文件路，使用该路径直接初始化session
     */
    public String copyDb2Job(String jobName) {
        String jobPath = path_data + jobName;//作业文件路径
        if (!FileUtils.isFileExists(jobPath)) {
            FileUtils.createOrExistsDir(jobPath);
        }
        String jobDbPathName = getJobDbPathName(jobName);//作业数据库路径
        //拷贝文件到作业文件目录
        FileUtils.copy(publicDbPathName, jobDbPathName);
        return jobDbPathName;
    }

    /**
     * 作业中使用的数据库，就是作业名称对应的文件，
     *
     * @param jobName 作业名称
     * @return 根据作业名称找到作业加密数据库路径
     */
    private String getJobDbPathName(String jobName) {
        String jobPath = path_data + jobName;
        return jobPath + File.separator + jobName + mm;//作业加密的数据库文件名称
    }

    /**
     * 3导出数据库
     * 根据作业名称，将加密数据库转换为加密文件
     * 数据库格式..->.temp ->.DB
     *
     * @param
     */
    public String db2File(String jobName) {
        String jobDbPathName = getJobDbPathName(jobName);//作业数据库路径

        String nameNotSuff = jobDbPathName.substring(0, jobDbPathName.lastIndexOf("."));

        String commonDb = nameNotSuff + temp;//解密后文件，果奔
        String jobOutDb = nameNotSuff + DB;
        if (FileUtils.isFileExists(commonDb)) {
            FileUtils.delete(commonDb);
        }
        if (FileUtils.isFileExists(jobOutDb)) {
            FileUtils.delete(jobOutDb);
        }

        /******************************************************************************************/
        DbEncryptUtils.decrypt(jobDbPathName, commonDb, "sw.12345");//解密数据库，加密的数据库转到明文

        boolean b = FileEncryptUtils.encrypt(commonDb, jobOutDb, "1234567812345678");//加密文件
        if (!b){
            throw new CommonException("加密数据库文件出错");
        }
        /******************************************************************************************/
        FileUtils.delete(commonDb);//删除临时文件，没有加密的数据库
        return jobOutDb;
    }

    public String getPublicDbPathName() {
        return publicDbPathName;
    }


}
