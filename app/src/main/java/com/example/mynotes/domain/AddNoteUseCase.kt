package com.example.mynotes.domain

class AddNoteUseCase(
    private val repository: NotesRepository
) {

   suspend operator fun invoke(title: String, content: String) {
        repository.addNote(title, content,isPinned = false, updatedAt = System.currentTimeMillis())
    }
}