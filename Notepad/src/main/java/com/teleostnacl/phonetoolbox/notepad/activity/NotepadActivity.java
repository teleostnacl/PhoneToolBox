package com.teleostnacl.phonetoolbox.notepad.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.teleostnacl.common.android.activity.BaseLogActivity;
import com.teleostnacl.phonetoolbox.notepad.R;
import com.teleostnacl.phonetoolbox.notepad.databinding.ActivityNotepadBinding;
import com.teleostnacl.phonetoolbox.notepad.util.NotepadUtils;

/**
 * Notepad 设置的Activity
 */
public class NotepadActivity extends BaseLogActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNotepadBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notepad);
        setSupportActionBar(binding.toolbar);

        binding.notepadMainSwitch.setChecked(NotepadUtils.notepadMainSwitch);

        // 配置通知转发总开关切换事件
        binding.notepadMainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotepadUtils.setNotepadMainSwitchFromSP(isChecked);
            NotepadUtils.updateNotepadService();
        });
    }
}
