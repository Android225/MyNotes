package com.example.mynotes.data

import android.content.Context
import com.example.mynotes.domain.ContentItem
import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NotesDao
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<ContentItem>,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val note = Note(0, title, content, updatedAt, isPinned)
        val noteDbModel = note.toDbModel()
        noteDao.addNote(noteDbModel)
    }

    override suspend fun deleteNote(noteId: Int) {
        noteDao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        noteDao.addNote(note.toDbModel())
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { it.ToEntities() }
    }

    override suspend fun getNote(noteId: Int): Note {
        return noteDao.getNote(noteId).ToEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { it.ToEntities() }
    }

    override suspend fun switchPinStatus(noteId: Int) {
        noteDao.switchPinnedStatus(noteId)
    }
}