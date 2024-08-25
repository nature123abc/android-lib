package com.dk.lib_dk.utils.file;

import android.graphics.Color;

public class FileInfo implements Comparable<FileInfo> {
    public int color = Color.BLACK;
    String jobName;
    String filePath;
    String creatTime;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    @Override
    public int compareTo(FileInfo fileInfo) {
        return fileInfo.getJobName().compareTo(this.getJobName());
    }
}
