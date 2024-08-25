package com.dk.lib_dk.db.comm;

import java.io.File;
import java.io.FileFilter;

public class DbFilter implements FileFilter {
    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName();
        if (fileName.toLowerCase().endsWith(".db") && pathname.isFile()) {
            return true;
        }
        return false;
    }
}