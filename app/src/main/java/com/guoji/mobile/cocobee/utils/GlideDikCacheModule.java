package com.guoji.mobile.cocobee.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;

import java.io.File;


/**
 * ClassName: GlideDiakCacheModule <br>
 * Description: 自定义Glide磁盘缓存<br>
 * Author: Cyarie <br>
 * Created: 2016/9/26 13:47 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class GlideDikCacheModule implements GlideModule {

    private String downloadDirectoryPath = Path.PROJECT_FILE_PATH+ "glide" + File.separator;

    @Override
    public void registerComponents(Context context, Glide glide) {

    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(
                new DiskLruCacheFactory(downloadDirectoryPath, AppConstants.DISK_MAX_CACHE_SIZE)
        );
    }
}
