package com.example.mynotes.data

import android.content.Context
import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl private constructor(context: Context) : NotesRepository {

    private val notesDatabase = NotesDatabase.getInstance(context)
    private val noteDao = notesDatabase.notesDao()


    override suspend fun addNote(
        title: String,
        content: String,
        isPinned: Boolean,
        updatedAt: Long
    ) {
        val noteDbModel = NoteDbModel(0, title, content, updatedAt, isPinned)
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

    companion object {

        private val LOCK = Any()
        private var instance: NotesRepositoryImpl? = null

        fun getInstance(context: Context): NotesRepositoryImpl {
            instance?.let { return it }

            synchronized(LOCK) {
                instance?.let { return it }

                return NotesRepositoryImpl(context).also {
                    instance = it }
            }
        }
    }
}