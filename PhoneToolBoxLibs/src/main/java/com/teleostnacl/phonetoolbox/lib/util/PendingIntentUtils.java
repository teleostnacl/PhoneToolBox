package com.teleostnacl.phonetoolbox.lib.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * PendingIntent工具类
 */
public class PendingIntentUtils {

    /**
     * 全局的REQUEST_CODE
     */
    private static final AtomicInteger REQUEST_CODE = new AtomicInteger(0x0911);


    /**
     * 获取一个新的REQUEST_CODE
     */
    public static synchronized int getRequestCode() {
        return REQUEST_CODE.incrementAndGet();
    }
}
