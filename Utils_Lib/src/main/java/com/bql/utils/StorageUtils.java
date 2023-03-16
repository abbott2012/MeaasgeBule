package com.bql.utils;

import java.io.File;

/**
 * ClassName: StorgeUtils <br>
 * Description: 存储工具类<br>
 * Author: Cyarie <br>
 * Created: 2016/8/25 08:51 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class StorageUtils {

    //    /**
    //     * 写外部存储权限
    //     */
    //    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";


    //    /**
    //     * 获取Cache目录
    //     *
    //     * @param context
    //     * @return
    //     */
    //    public static File getCacheDirectory(Context context) {
    //        return getCacheDirectory(context, true);
    //    }

    //    /**
    //     * 获取Cache目录
    //     *
    //     * @param context
    //     * @param preferExternal
    //     * @return
    //     */
    //    public static File getCacheDirectory(Context context, boolean preferExternal) {
    //        File appCacheDir = null;
    //        if (preferExternal && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
    //            appCacheDir = getExternalCacheDir(context);
    //        }
    //        if (appCacheDir == null) {
    //            appCacheDir = context.getCacheDir();
    //        }
    //        if (appCacheDir == null) {
    //            return new File("/data/data/" + context.getPackageName() + "/cache/");
    //        }
    //        return appCacheDir;
    //    }

    //    public static File getOwnCacheDirectory(Context context, String cacheDir) {
    //        File appCacheDir = null;
    //        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
    //            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
    //        }
    //        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
    //            return context.getCacheDir();
    //        }
    //        return appCacheDir;
    //    }

    //    /**
    //     * 获取外部Cache目录
    //     *
    //     * @param context
    //     * @return
    //     */
    //    private static File getExternalCacheDir(Context context) {
    //        File appCacheDir = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), SDCardUtils.MAIN_DIR_PREFIX), Constants.LOGTAG), context.getPackageName()), SDCardUtils.CACHE_DIR_NAME);
    //        if (appCacheDir.exists()) {
    //            return appCacheDir;
    //        }
    //        if (!appCacheDir.mkdirs()) {
    //            return null;
    //        }
    //        try {
    //            new File(appCacheDir, ".nomedia").createNewFile();
    //            return appCacheDir;
    //        } catch (IOException e) {
    //            CLog.e(e);
    //            return appCacheDir;
    //        }
    //    }

    //    private static boolean hasExternalStoragePermission(Context context) {
    //        return context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    //    }


    /**
     * 获取指定文件夹总大小
     *
     * @param file
     * @param sizeAdded 已经计算的大小
     * @return
     */
    public static long getTotalSizeInDir(File file, long sizeAdded) {
        long totalSize = sizeAdded;
        if (file != null && file.exists() && file.isDirectory()) {
            try {
                for (File child : file.listFiles()) {
                    if (child.isDirectory()) {
                        totalSize = getTotalSizeInDir(child, totalSize);
                    } else {
                        totalSize += child.length();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return totalSize;
    }

    /**
     * 清除Cache文件夹
     *
     * @param dir
     * @param deletedFilesSize 删除的文件大小
     * @return
     */
    public static long clearCacheFolder(File dir, long deletedFilesSize) {
        long deletedFiles = deletedFilesSize;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles = clearCacheFolder(child, deletedFiles);
                    } else if (child.delete()) {
                        deletedFiles += child.length();
                    }
                }
                dir.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

}
