package com.example.mynotes.data

import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow

class TestNotesRepositoryImpl : NotesRepository {
    override fun addNote(note: Note) {
        TODO("Not yet implemented")
    }

    override fun deleteNote(noteId: Int) {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note) {
        TODO("Not yet implemented")
    }

    override fun getAllNotes(): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getNote(noteId: Int): Note {
        TODO("Not yet implemented")
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun switchPinStatus(noteId: Int) {
        TODO("Not yet implemented")
    }
}