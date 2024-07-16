package com.teleostnacl.phonetoolbox.notepad.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teleostnacl.common.android.context.ContextUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 保存笔记数据的数据库
 */
@Database(entities = {NoteEntry.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase database;

    private static final String DATABASE_NAME = "note.db";

    /**
     * 单线程读写数据库 保证数据库稳健
     */
    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static synchronized NoteDatabase getInstance() {
        if (database == null) database = Room.databaseBuilder(ContextUtils.getContext(),
                        NoteDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
        return database;
    }

    public abstract NoteDao noteDao();
}
