package com.teleostnacl.phonetoolbox.lib.crash;

import android.os.Bundle;
import android.widget.TextView;

import com.teleostnacl.phonetoolbox.lib.R;
import com.teleostnacl.phonetoolbox.lib.activity.BaseActivity;

/**
 * 崩溃时的Activity
 */
public class CrashActivity extends BaseActivity {

    public static final String ARG_CRASH_MESSAGE = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        setSupportActionBar(findViewById(R.id.tool_bar));

        TextView textView = findViewById(R.id.text_view);

        // 设置崩溃日志
        textView.setText(getIntent().getStringExtra(ARG_CRASH_MESSAGE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        finishAndRemoveTask();
    }
}