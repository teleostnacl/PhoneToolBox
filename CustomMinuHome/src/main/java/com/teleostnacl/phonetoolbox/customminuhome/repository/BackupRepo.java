package com.teleostnacl.phonetoolbox.customminuhome.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.common.android.utils.AndroidFileUtils;
import com.teleostnacl.common.java.util.FileUtils;
import com.teleostnacl.common.java.util.IOUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;


/**
 * 处理备份文件的Repo类
 */
public class BackupRepo {

    private static final String TAG = "BackupRepo";

    /**
     * 定义备份文件前缀的内容
     */
    private static final byte[] BACKUP_FILE_PREFIX = new byte[]{
            0x4D, 0x49, 0x55, 0x49, 0x20, 0x42, 0x41, 0x43, 0x4B, 0x55, 0x50, 0x0A, 0x32, 0x0A, 0x63, 0x6F, 0x6D, 0x2E,
            0x6D, 0x69, 0x75, 0x69, 0x2E, 0x68, 0x6F, 0x6D, 0x65, 0x20, (byte) 0xE7, (byte) 0xB3, (byte) 0xBB, (byte) 0xE7, (byte) 0xBB, (byte) 0x9F, (byte) 0xE6, (byte) 0xA1,
            (byte) 0x8C, (byte) 0xE9, (byte) 0x9D, (byte) 0xA2, 0x0A, 0x2D, 0x31, 0x0A, 0x30, 0x0A, 0x41, 0x4E, 0x44, 0x52, 0x4F, 0x49, 0x44, 0x20,
            0x42, 0x41, 0x43, 0x4B, 0x55, 0x50, 0x0A, 0x35, 0x0A, 0x30, 0x0A, 0x6E, 0x6F, 0x6E, 0x65, 0x0A};

    /**
     * 存放备份的文件夹
     */
    private static final File BACKUP_FOLDER = new File(ContextUtils.getCacheDir(), "backup");

    /**
     * 存放原始备份的数据文件
     */
    private static final File ORIGIN_BACKUP_FILE = new File(BACKUP_FOLDER, "origin");

    /**
     * 存放备份解压文件
     */
    private static final File DECOMPRESS_BACK_FOLDER = new File(BACKUP_FOLDER, "decompress");

    /**
     * 存放重新打包之后的文件
     */
    private static final File OUTPUT_BACKUP_FILE = new File(BACKUP_FOLDER, "系统桌面(com.miui.home).bak");


    /**
     * 检查所选择的文件是否存在
     *
     * @param uri 打开文件所呈现的uri
     * @return 返回最终文件复制的路径 如果检查失败 则返回空
     */
    @Nullable
    public String checkBackupFileCorrect(@NonNull Uri uri) {
        Logger.v(TAG, "checkBackupFileCorrect() uri = " + uri);

        // 删除之前的文件
        FileUtils.delete(BACKUP_FOLDER);
        // 创建文件夹
        if (!BACKUP_FOLDER.mkdirs()) {
            Logger.v(TAG, "checkBackupFileCorrect() BACKUP_FOLDER.mkdirs failed");
            return null;
        }
        // 拷贝文件
        if (!AndroidFileUtils.copyFile(uri, ORIGIN_BACKUP_FILE) || !ORIGIN_BACKUP_FILE.exists()) {
            Logger.v(TAG, "checkBackupFileCorrect() copyBackFile failed");
            return null;
        }

        // 解压文件
        if (!decompressBackupFile(ORIGIN_BACKUP_FILE, DECOMPRESS_BACK_FOLDER)) {
            Logger.v(TAG, "decompressBackupFile copyBackFile failed");
            return null;
        }

        return DECOMPRESS_BACK_FOLDER.getAbsolutePath();
    }

    /**
     * 更改完成之后, 重新打包为tar, 并添加偏移量
     *
     * @param file 需打包的文件夹
     * @return 是否成功
     */
    public boolean recompressBackFile(File file) {
        // Create TarOutputStream
        try (FileOutputStream fos = new FileOutputStream(OUTPUT_BACKUP_FILE);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(bos)) {

            bos.write(BACKUP_FILE_PREFIX);
            taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            taos.setAddPaxHeadersForNonAsciiNames(true);

            // Add the source folder content to the tar file
            addFilesToTar(taos, file, "");

            taos.finish();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void addFilesToTar(@NonNull TarArchiveOutputStream taos, @NonNull File file, String parent) throws Exception {
        Logger.v(TAG, "addFilesToTar() file = " + file.getAbsolutePath() + ", parent = " + parent);
        String entryName = parent + file.getName();
        TarArchiveEntry entry = new TarArchiveEntry(file, entryName);

        taos.putArchiveEntry(entry);

        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    taos.write(buffer, 0, length);
                }
            }
            taos.closeArchiveEntry();
        } else if (file.isDirectory()) {
            taos.closeArchiveEntry();
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFilesToTar(taos, child, entryName + "/");
                }
            }
        }
    }

    /**
     * 解压备份的数据文件
     * <p>
     * 备份文件使用的是tar压缩格式 偏移量为70字节
     *
     * @param file      备份文件
     * @param outputDir 目标文件夹
     * @return 是否成功
     */
    private boolean decompressBackupFile(@NonNull File file, @NonNull File outputDir) {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            Logger.v(TAG, "decompressBackupFile() mkdirs failed");
            return false;
        }

        FileInputStream tarFileInputStream = null;
        TarArchiveInputStream tarArchiveInputStream = null;
        try {
            tarFileInputStream = new FileInputStream(file);
            tarFileInputStream.skip(BACKUP_FILE_PREFIX.length);
            tarArchiveInputStream = new TarArchiveInputStream(tarFileInputStream);

            TarArchiveEntry entry;
            while ((entry = tarArchiveInputStream.getNextEntry()) != null) {
                File entryFile = new File(outputDir, entry.getName());
                Logger.v(TAG, "decompressBackupFile() entryFile = " + entryFile.getAbsolutePath());

                if (entry.isDirectory()) {
                    if (!entryFile.exists() && !entryFile.mkdirs()) {
                        Logger.v(TAG, "decompressBackupFile() entryFile.mkdirs failed!");
                        return false;
                    }
                } else {
                    File parentFile = entryFile.getParentFile();
                    if (parentFile == null || (!parentFile.exists() && !parentFile.mkdirs())) {
                        Logger.v(TAG, "decompressBackupFile() entryFile.parentFile.mkdirs failed! parent = " + parentFile);
                        return false;
                    }
                    try (OutputStream outputStream = Files.newOutputStream(entryFile.toPath())) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = tarArchiveInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            Logger.v(TAG, "decompressBackupFile() decompress to tar failed! e = " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.close(tarArchiveInputStream, tarFileInputStream);
        }

//        try {
//            Process process = Runtime.getRuntime().exec("tar -xvf " + file.getAbsolutePath() +
//                    " -C " + file.getAbsolutePath());
//            int exitValue = process.waitFor();
//            return exitValue == 0;
//        } catch (Exception e) {
//            Logger.v(TAG, "decompressBackupFile() decompress failed e = " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }


    }

}
