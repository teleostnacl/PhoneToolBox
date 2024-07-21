package com.teleostnacl.phonetoolbox.customminuhome.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teleostnacl.common.android.context.ToastUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.phonetoolbox.customminuhome.R;
import com.teleostnacl.phonetoolbox.customminuhome.databinding.FragmentMainBinding;
import com.teleostnacl.phonetoolbox.customminuhome.model.MainViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * 主Fragment
 */
public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private MainViewModel mainViewModel;
    private FragmentMainBinding binding;

    private final CompositeDisposable disposable = new CompositeDisposable();

    /**
     * 选取备份文件的Launcher
     */
    private ActivityResultLauncher<Intent> backupFilePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // 注册选取备份文件的Launcher
        backupFilePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Logger.v(TAG, "backupFilePickerLauncher result = " + result.getResultCode());
                    // 正常状态 则读取文件
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            // Handle the selected file URI
                            if (uri != null) {
                                binding.mainFragmentChooseBackupFileLoading.setVisibility(View.VISIBLE);
                                // 检查选择的文件是否正确
                                disposable.add(mainViewModel.checkBackupFileCorrect(uri).subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        Logger.v("TAG", "success");
                                    } else {
                                        ToastUtils.makeToast(R.string.main_fragment_choose_backup_file_check_failed);
                                    }
                                    binding.mainFragmentChooseBackupFileLoading.setVisibility(View.GONE);
                                }));
                                return;
                            }
                        }
                    }

                    ToastUtils.makeToast(R.string.main_fragment_choose_backup_file_failed);
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        // 打开备份应用
        binding.mainFragmentOpenBackupApp.setOnClickListener(v -> {
            mainViewModel.openBackupApp();
            ToastUtils.makeToast(R.string.main_fragment_open_backup_app_tips);
        });

        // 选择备份文件
        binding.mainFragmentOpenBackupFile.setOnClickListener(v -> {
            // 启动选取文件的Launcher
            if (backupFilePickerLauncher != null) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                backupFilePickerLauncher.launch(intent);
                ToastUtils.makeLongToast(R.string.main_fragment_choose_backup_file_tips);
            }

        });

        // 整理
        binding.mainFragmentModify.setOnClickListener(v ->
                disposable.add(mainViewModel.modify().subscribe((aBoolean, throwable) ->
                        Logger.v(TAG, "modify success = " + aBoolean))));

        // 保存
        binding.mainFragmentSave.setOnClickListener(v -> {
            disposable.add(mainViewModel.save().subscribe((aBoolean, throwable) -> {
                Logger.v(TAG, "save success = " + aBoolean);
                if (aBoolean) {

                }
            }));
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置获取系统信息
        binding.setSummaryModel(mainViewModel.summaryModel);
    }
}