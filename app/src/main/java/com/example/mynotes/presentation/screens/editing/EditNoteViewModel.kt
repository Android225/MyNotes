package com.example.mynotes.presentation.screens.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.TestNotesRepositoryImpl
import com.example.mynotes.domain.DeleteNoteUseCase
import com.example.mynotes.domain.EditNoteUseCase
import com.example.mynotes.domain.GetNoteUseCase
import com.example.mynotes.domain.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(private val noteId: Int,context: Context) : ViewModel(){

    private val repository = TestNotesRepositoryImpl
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId)
                EditNoteState.Editing(note)
            }
        }
    }
}

sealed interface EditNoteState{

    data object Initial: EditNoteState

    data class Editing(
        val note: Note
    ): EditNoteState {

        val isSaveEnable: Boolean
            get() = note.title.isNotBlank() && note.content.isNotBlank()
    }

    data object Finished: EditNoteState
}