package com.example.mynotes.data

import com.example.mynotes.domain.Note

fun Note.toDbModel(): NoteDbModel {
    return NoteDbModel(id, title, content, updatedAt, isPinned)
}

fun NoteDbModel.ToEntity(): Note {
    return Note(id, title, content, updatedAt, isPinned)
}

fun List<NoteDbModel>.ToEntities(): List<Note>{
    return this.map { it.ToEntity() }
}
