package com.teleostnacl.phonetoolbox.custommiuihome;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teleostnacl.phonetoolbox.custommiuihome.model.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(findViewById(R.id.main_toolbar));

        //设置toolbar中的MiuiHome为渐变色
        TextView textView = findViewById(R.id.main_toolbar_title_miui_home);
        textView.post(() -> {
            textView.getPaint().setShader(new LinearGradient(
                    0, 0, textView.getWidth() / 2.0f, textView.getHeight() / 2.0f,
                    getColor(R.color.main_toolbar_title_miui_home_start_color),
                    getColor(R.color.main_toolbar_title_miui_home_end_color),
                    Shader.TileMode.CLAMP));

            findViewById(R.id.main_toolbar_title_custom).setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        });
    }

}