package com.teleostnacl.phonetoolbox.notepad.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.common.java.util.ExecutorServiceUtils;
import com.teleostnacl.common.java.util.FileUtils;
import com.teleostnacl.common.java.util.IOUtils;
import com.teleostnacl.common.java.util.RandomUtils;
import com.teleostnacl.phonetoolbox.notepad.database.NoteDao;
import com.teleostnacl.phonetoolbox.notepad.database.NoteDatabase;
import com.teleostnacl.phonetoolbox.notepad.model.Note;

import org.eclipse.jgit.api.Git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.MaybeSubject;
import io.reactivex.rxjava3.subjects.SingleSubject;

/**
 * Repo类, 用于获取和管理数据
 */
public class NotepadRepository {

    private static final String TAG = "NotepadRepository";

    /**
     * 放置笔记的文件夹
     */
    private static final File NOTEPAD_FOLDER = new File(ContextUtils.getContext().getFilesDir(), "notepad");

    // region git相关常量
    /**
     * git仓库的链接
     */
    private static final String GITEE_URL = "https://gitee.com/teleostnacl/Notepad.git";

    /**
     * 分支名
     */
    private static final String BRANCH_NAME = "release";
    // endregion

    // region 笔记相关常量
    /**
     * 每条Notepad的前缀
     */
    private static final String NOTE_PREFIX = "- ";
    // endregion

    /**
     * 读写数据库的Dao
     */
    private final NoteDao noteDao = NoteDatabase.getInstance().noteDao();

    /**
     * 从git 拉数据
     */
    public Maybe<Object> gitClone() {
        return Maybe.create(emitter -> {
            Git git = null;
            try {
                // 创建文件夹
                createFolder();

                // 克隆仓库
                git = Git.cloneRepository()
                        .setDirectory(NOTEPAD_FOLDER)
                        .setURI(GITEE_URL)
                        .setBranchesToClone(List.of("refs/heads/" + BRANCH_NAME))
                        .setBranch(BRANCH_NAME)
                        .call();

            } catch (Exception e) {
                emitter.onError(e);
            } finally {
                IOUtils.close(git);
            }
            Logger.v(TAG, "git success!");
            emitter.onSuccess(new Object());
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 处理笔记数据
     */
    public Maybe<Object> handleNotepad() {
        return Maybe.create(emitter -> {
            List<Note> noteList = new ArrayList<>();

            File[] files = NOTEPAD_FOLDER.listFiles();

            if (files != null) {
                for (File file : files) {
                    handleFile(file, noteList);
                }
            }

            // 存进数据库中
            saveNotepadIntoDatabase(noteList);

            emitter.onSuccess(new Object());
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 获取一条笔记
     */
    public Maybe<Note> getNote() {
        return getNoteSize()
                .filter(integer -> integer > 0)
                .flatMap(integer -> getNoteByInt(RandomUtils.nextInt(integer)))
                .onErrorComplete();
    }

    /**
     * 获取笔记数量
     */
    @NonNull
    private Single<Integer> getNoteSize() {
        SingleSubject<Integer> subject = SingleSubject.create();
        NoteDatabase.executor.execute(() -> subject.onSuccess(noteDao.getSize()));
        return subject;
    }

    /**
     * 获取指定位置的笔记
     */
    @NonNull
    private Maybe<Note> getNoteByInt(int index) {
        MaybeSubject<Note> subject = MaybeSubject.create();
        NoteDatabase.executor.execute(() -> subject.onSuccess(noteDao.getNoteByInt(index)));
        return subject;
    }

    /**
     * 创建文件夹
     */
    private void createFolder() {
        // 删除
        FileUtils.delete(NOTEPAD_FOLDER);
        // 创建文件夹
        //noinspection ResultOfMethodCallIgnored
        NOTEPAD_FOLDER.mkdirs();
    }

    /**
     * 处理文件 区分文件或文件夹
     */
    private void handleFile(@NonNull File file, List<Note> notes) {
        // 文件不存在 不处理
        if (!file.exists()) {
            return;
        }

        // 是文件 直接读取
        if (file.isFile()) {
            readNotepads(file, notes);
            return;
        }

        // 是文件夹 列出文件 并遍历
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                handleFile(f, notes);
            }
        }
    }

    /**
     * 从文件夹中读取Notepad
     */
    private void readNotepads(@NonNull File file, List<Note> notes) {
        // 不存在 或 非文件 不读取
        if (!file.exists() || !file.isFile()) {
            return;
        }

        String from = file.getName().replace(".md", "");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // 读取文件
            while ((line = reader.readLine()) != null) {
                // 必须是- 开头
                if (!line.startsWith(NOTE_PREFIX)) {
                    continue;
                }

                String content = line.replaceAll(NOTE_PREFIX, "").trim();

                // 过滤空内容
                if (TextUtils.isEmpty(content)) {
                    continue;
                }

                Note note = new Note();
                note.note = content;
                note.from = from;

                notes.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将笔记数据存入数据库中
     */
    private void saveNotepadIntoDatabase(List<Note> notes) {
        // 使用executor 保证数据库稳健 并阻塞
        ExecutorServiceUtils.submitByExecutor(NoteDatabase.executor, (Callable<Void>) () -> {
            // 先清空数据库
            noteDao.deleteAll();

            // 存入数据库中
            for (Note note : notes) {
                noteDao.insertOrUpdate(note);
            }
            return null;
        }, () -> null);
    }
}
