package com.example.mynotes.domain

class AddNoteUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(note: Note){
        repository.addNote(note)
    }
}