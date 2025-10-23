package com.example.mynotes.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(noteId: Int){
        repository.switchPinStatus(noteId)
    }
}