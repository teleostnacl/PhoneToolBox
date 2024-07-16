package com.teleostnacl.phonetoolbox.lib.screenaction;

import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * 记录屏幕开启和关闭的工具类
 */
public class ScreenActionUtils {
    /**
     * 屏幕开启状态
     */
    public static boolean screenAction = true;

    /**
     * 屏幕开启时的观察者, 可订阅屏幕开启事件, 执行自定义人任务
     */
    public static PublishSubject<Boolean> screenActionPublishSubject = PublishSubject.create();
}
