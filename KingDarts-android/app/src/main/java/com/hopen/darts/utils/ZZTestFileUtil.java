package com.hopen.darts.utils;

import android.text.TextUtils;

import com.hopen.darts.base.C;

import java.io.File;

public class ZZTestFileUtil {

    public static void check(final Callback callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int times = 0, same_times = 0;
                    long total, last_total = 0;
                    while (times < 60 && same_times <= 5) {
                        File folder = new File(C.app.file_path);
                        total = getTotalSizeOfFilesInDir(folder);
                        if (total >= 104857600) break;//100M
                        if (total == last_total) {
                            same_times++;
                        } else {
                            last_total = total;
                            same_times = 0;
                        }
                        times++;
                        Thread.sleep(2000);
                    }
                    String sn = PhoneUtil.readSNCode();
                    if (!TextUtils.isEmpty(sn) && !TextUtils.equals(sn, C.app.no_sn))
                        callback.onOver(true);
                    else
                        callback.onOver(false);
                } catch (Throwable e) {
                    e.printStackTrace();
                    try {
                        callback.onOver(false);
                    } catch (Throwable e1) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // 递归方式 计算文件的大小
    private static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    public interface Callback {
        void onOver(boolean isEligible);
    }
}
