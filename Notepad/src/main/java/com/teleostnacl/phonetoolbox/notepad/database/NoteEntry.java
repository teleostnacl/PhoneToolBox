package com.teleostnacl.phonetoolbox.notepad.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.teleostnacl.common.android.database.BaseEntry;
import com.teleostnacl.common.android.utils.EncryptUtils;
import com.teleostnacl.phonetoolbox.notepad.model.Note;

/**
 * 笔记条目
 */
@Entity(tableName = "note", primaryKeys = {"note", "from"})
public class NoteEntry implements BaseEntry<Note> {

    /**
     * 笔记内容
     */
    @NonNull
    @ColumnInfo(name = "note")
    public String note;

    /**
     * 来源
     */
    @NonNull
    @ColumnInfo(name = "from")
    public String from;

    public NoteEntry(@NonNull String note, @NonNull String from) {
        this.note = note;
        this.from = from;
    }

    @Ignore
    public NoteEntry(@NonNull Note note) {
        this.note = EncryptUtils.encrypt(note.note);
        this.from = EncryptUtils.encrypt(note.from);
    }

    @Override
    public Note toModel() {
        Note note = new Note();

        note.note = EncryptUtils.decrypt(this.note);
        note.from = EncryptUtils.decrypt(this.from);

        return note;
    }
}
