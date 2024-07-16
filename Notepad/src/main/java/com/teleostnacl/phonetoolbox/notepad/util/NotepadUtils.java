package com.teleostnacl.phonetoolbox.notepad.util;

import android.content.SharedPreferences;

import com.teleostnacl.common.android.context.SPUtils;

public class NotepadUtils {

    /**
     * 通知转发SP的文件名
     */
    private static final String SP_FILE_NAME = "notepad";

    /**
     * SP key 一言总开关的Key
     */
    private static final String KEY_NOTEPAD_MAIN_SWITCH = "notepad_main_switch";

    /**
     * 读写SP的SharedPreferences
     */
    private static final SharedPreferences sharedPreferences = SPUtils.getSP(SP_FILE_NAME);

    /**
     * 记录通知转发总开关
     */
    public static boolean notepadMainSwitch = getNotepadMainSwitchFromSP();

    /**
     * @return 从SP中获取一言总开关的状态
     */
    public static boolean getNotepadMainSwitchFromSP() {
        return SPUtils.getBoolean(sharedPreferences, KEY_NOTEPAD_MAIN_SWITCH, true);
    }

    /**
     * 向SP设置一言总开关的状态
     */
    public static void setNotepadMainSwitchFromSP(boolean open) {
        notepadMainSwitch = open;
        SPUtils.putBoolean(sharedPreferences.edit(), KEY_NOTEPAD_MAIN_SWITCH, open);
    }

    /**
     * 更新笔记服务
     */
    public static void updateNotepadService() {

    }
}
