package com.denspark.utils.file_path_utils;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

public class FilePathUtils {


    private static FilePathUtils mInstance;

    private FilePathUtils() {
    }

    public static FilePathUtils getInstance() {
        if (mInstance == null) {
            mInstance = new FilePathUtils();
        }
        return mInstance;
    }

    public String getFullPath(String relativePath, boolean debugMode){
        String path = mInstance.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        String correctPath = path.substring(1, path.lastIndexOf("/") + 1);
        if (debugMode) {
            System.out.println("Path: " + path);
        }
        String correctPath = path.substring(0, path.lastIndexOf("/"));
        if (SystemUtils.IS_OS_WINDOWS) {
            correctPath = path.substring(1, path.lastIndexOf("/"));
        }
        if (!(path.contains(".jar"))) {
            correctPath = correctPath.substring(0, correctPath.lastIndexOf("/"));
        }
        correctPath = correctPath.substring(0, correctPath.lastIndexOf("/") + 1);
        if (debugMode) {
            System.out.println("correctPath: " + correctPath);
        }
        String fullPath = correctPath + relativePath;
        if (debugMode) {
            System.out.println("fullPath: " + fullPath);
        }
        return fullPath;
    }
    public boolean fileExist(String path){
        File tempFile = new File(path);
        return tempFile.isFile();
    }
}
