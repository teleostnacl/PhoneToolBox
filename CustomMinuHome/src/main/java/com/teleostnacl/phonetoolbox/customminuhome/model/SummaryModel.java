package com.teleostnacl.phonetoolbox.customminuhome.model;

import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.DesktopModel;

/**
 * 概览所使用的model
 */
public class SummaryModel {
    // 记录桌面数据的model
    public DesktopModel desktopModel = new DesktopModel();

    // 记录系统及软件信息的model
    public SystemModel systemModel = new SystemModel();
}
