package com.teleostnacl.phonetoolbox.notepad.database;

import androidx.room.Dao;
import androidx.room.Query;

import com.teleostnacl.common.android.database.CRUDao;
import com.teleostnacl.phonetoolbox.notepad.model.Note;

@Dao
public interface NoteDao extends CRUDao<NoteEntry, Note> {

    /**
     * 删除所有数据
     */
    @Query("DELETE FROM note")
    void deleteAll();

    /**
     * 统计数量
     */
    @Query("SELECT COUNT(*) FROM note")
    int getSize();

    /**
     * 获取指定位置的笔记
     */
    @Query("SELECT * FROM note  limit :index, 1")
    NoteEntry getNoteByIntFromDatabase(int index);

    /**
     * 获取指定位置的笔记
     */
    default Note getNoteByInt(int index) {
        return getNoteByIntFromDatabase(index).toModel();
    }
}
