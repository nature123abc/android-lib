package com.dk.lib_dk.db.encry;

import com.blankj.utilcode.util.StringUtils;
import com.dk.error.CommonException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Created by hq on 2018/2/5.
 * @描述：加密解密数据库 s
 * @创建人：hq
 */

public class FileEncryptUtils {
    /**
     * 加密文件，数据库
     *
     * @param srcFile  源文件目录
     * @param destFile 目标目录
     * @param key      必须是16位的密码
     * @return
     */
    public static boolean encrypt(String srcFile, String destFile, String key) {
        if (StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile) || StringUtils.isEmpty(key)) {
            return false;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            Key privateKey = new SecretKeySpec(key.getBytes(), "AES");

            SecureRandom sr = new SecureRandom();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec spec = new IvParameterSpec(privateKey.getEncoded());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, spec, sr);

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);

            byte[] b = new byte[2048];

            while (fis.read(b) != -1) {
                fos.write(cipher.doFinal(b));
                fos.flush();
            }

            fos.close();
            fis.close();
            return true;
        } catch (Exception e) {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != fos) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new CommonException(e.getMessage());
        }

    }

    /**
     * 解密文件（数据库 )
     *
     * @param srcFile  需要解密的目录
     * @param destFile 解密的目录
     * @return 解密是否成功
     */
    public static boolean decrypt(String srcFile, String destFile, String key) {
        if (StringUtils.isEmpty(srcFile) || StringUtils.isEmpty(destFile) || StringUtils.isEmpty(key)) {
            return false;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            Key privateKey = new SecretKeySpec(key.getBytes(), "AES");

            SecureRandom sr = new SecureRandom();
            Cipher ciphers = Cipher.getInstance("AES/CBC/PKCS5Padding");

            IvParameterSpec spec = new IvParameterSpec(privateKey.getEncoded());
            ciphers.init(Cipher.DECRYPT_MODE, privateKey, spec, sr);

              fis = new FileInputStream(srcFile);
              fos = new FileOutputStream(destFile);
            byte[] b = new byte[2064];
            while (fis.read(b) != -1) {
                fos.write(ciphers.doFinal(b));
                fos.flush();
            }
            fos.close();
            fis.close();

            return true;
        } catch (Exception e) {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != fos) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            throw new CommonException(e.getMessage());
        }

    }


}
