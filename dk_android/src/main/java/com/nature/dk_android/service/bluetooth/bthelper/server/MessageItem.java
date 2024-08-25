package com.nature.dk_android.service.bluetooth.bthelper.server;

/**
 * Created by wuhaojie on 2016/9/10 20:23.
 */
public class MessageItem {
    String text;
    char[] data;
    TYPE mTYPE;

    public MessageItem(String text) {
        this.text = text;
        this.mTYPE = TYPE.STRING;
    }

    public MessageItem(char[] data) {
        this.data = data;
        this.mTYPE = TYPE.CHAR;
    }

    static enum TYPE {
        STRING,
        CHAR;

        private TYPE() {
        }
    }
}
