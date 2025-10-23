package com.example.mynotes.data

import com.example.mynotes.domain.Note
import com.example.mynotes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestNotesRepositoryImpl : NotesRepository {

    private val testData = mutableListOf<Note>().apply {
        repeat(10){
            add(Note(it,"Title$it","Content$it",System.currentTimeMillis(),false))
        }
    }

    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    override fun addNote(note: Note) {
        notesListFlow.update {
           it + note
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf {
                    it.id == noteId
                }
            }
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update {oldList ->
            oldList.map {
                if(it.id == note.id){
                    note
                } else {
                    it
                }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
       return notesListFlow.value.first { it.id == noteId }
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return notesListFlow.map { currentList ->
            currentList.filter {
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override fun switchPinStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {
                if(it.id == noteId){
                    it.copy(isPinned = !it.isPinned)
                } else {
                    it
                }
            }
        }
    }
}