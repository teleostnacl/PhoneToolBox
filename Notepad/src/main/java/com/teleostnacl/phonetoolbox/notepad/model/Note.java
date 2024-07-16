package com.teleostnacl.phonetoolbox.notepad.model;

import com.teleostnacl.common.android.database.BaseModel;
import com.teleostnacl.phonetoolbox.notepad.database.NoteEntry;

public class Note implements BaseModel<NoteEntry> {
    /**
     * 笔记内容
     */
    public String note;

    /**
     * 来源
     */
    public String from;

    @Override
    public NoteEntry toEntry() {
        return new NoteEntry(this);
    }
}
